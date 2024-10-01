package net.hyze.core.shared.party.cache.redis;

import com.google.common.primitives.Ints;
import net.hyze.core.shared.cache.redis.RedisCache;
import net.hyze.core.shared.party.Party;
import net.hyze.core.shared.providers.RedisProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PartyRedisCache implements RedisCache {

    private final RedisProvider provider;
    
    private static String buildUserPartyKey(@NonNull Integer userId) {
        return String.format("partyuser:%d", userId);
    }

    private static String buildPartyKey(Integer id) {
        return String.format("party:%d", id);
    }
    
    private static String buildMembersKey(Integer partyId) {
        return String.format("partymembers:%d", partyId);
    }
    
    private static String buildInvitationsKey(Integer userId) {
        return String.format("partyinvitations:%d", userId);
    }

    // Setters
    public Integer nextId() {
        try (Jedis jedis = this.provider.provide().getResource()) {
            return jedis.incr("partyid").intValue();
        }
    }

    public void initializeParty(Party party, Integer targetId) {
        try (Jedis jedis = provider.provide().getResource()) {
            Pipeline pipeline = jedis.pipelined();
            pipeline.hset(buildPartyKey(party.getId()), "leader", String.valueOf(party.getLeader()));
            pipeline.sadd(buildMembersKey(party.getId()), String.valueOf(party.getLeader()));
            pipeline.hset(buildUserPartyKey(party.getLeader()), "party", String.valueOf(party.getId()));
            pipeline.sadd(buildInvitationsKey(targetId), String.valueOf(party.getId()));
            pipeline.expire(buildInvitationsKey(targetId), 300);
            pipeline.sync();
        }
    }

    public void destroyParty(Party party) {
        try (Jedis jedis = this.provider.provide().getResource()) {
            Pipeline pipeline = jedis.pipelined();
            destroyParty(party, pipeline);
            pipeline.sync();
        }
    }

    public void destroyParty(Party party, Pipeline pipeline) {
        pipeline.del(buildPartyKey(party.getId()));
        pipeline.del(buildMembersKey(party.getId()));

        for(Integer member : party.getMembers()) {
            pipeline.hdel(buildUserPartyKey(member), "party");
        }
    }

    public void addInvitation(Party party, Integer targetId) {
        try (Jedis jedis = this.provider.provide().getResource()) {
            jedis.sadd(buildInvitationsKey(targetId), String.valueOf(party.getId()));
        }
    }

    public void removeInvitation(Party party, Integer targetId) {
        try (Jedis jedis = this.provider.provide().getResource()) {
            Pipeline pipeline = jedis.pipelined();
            removeInvitation(party, targetId, pipeline);

            if(party.getMembers().size() <= 1) {
                destroyParty(party, pipeline);
            }

            pipeline.sync();
        }
    }

    public void removeInvitation(Party party, Integer targetId, Pipeline pipeline) {
        pipeline.srem(buildInvitationsKey(targetId), String.valueOf(party.getId()));
    }

    public void setLeader(Party party, Integer targetId) {
        try (Jedis jedis = this.provider.provide().getResource()) {
            jedis.hset(buildPartyKey(party.getId()), "leader", String.valueOf(targetId));
        }
    }

    public void addMember(Party previousParty, Party party, Integer targetId) {
        try (Jedis jedis = this.provider.provide().getResource()) {
            Pipeline pipeline = jedis.pipelined();

            if(previousParty != null) {
                pipeline.srem(buildMembersKey(previousParty.getId()), String.valueOf(targetId));
            }

            removeInvitation(party, targetId, pipeline);
            pipeline.sadd(buildMembersKey(party.getId()), String.valueOf(targetId));
            pipeline.hset(buildUserPartyKey(targetId), "party", String.valueOf(party.getId()));
            pipeline.sync();
        }
    }

    public void removeMember(Party party, Integer targetId) {
        try (Jedis jedis = this.provider.provide().getResource()) {
            Pipeline pipeline = jedis.pipelined();
            pipeline.srem(buildMembersKey(party.getId()), String.valueOf(targetId));
            pipeline.hdel(buildUserPartyKey(targetId), "party");

            if(party.getMembers().size() <= 1) {
                destroyParty(party, pipeline);
            }

            pipeline.sync();
        }
    }

    // Init
    public Party get(Integer partyId) {
        try (Jedis jedis = this.provider.provide().getResource()) {
            Pipeline pipeline = jedis.pipelined();

            Supplier<Party> supplier = get(partyId, pipeline);
            pipeline.sync();

            return supplier.get();
        }
    }

    public Supplier<Party> get(Integer partyId, Pipeline pipeline) {
        Response<String> leader = getLeader(partyId, pipeline);
        Response<Set<String>> members = getMembers(partyId, pipeline);

        return () -> {
            Party party = new Party(partyId);
            party.setLeader(Ints.tryParse(leader.get()));
            party.addMembers(members.get().stream().map(Ints::tryParse).toArray(Integer[]::new));
            return party;
        };
    }

    // Leader

    public Response<String> getLeader(Integer partyId, Pipeline pipeline) {
        return pipeline.hget(buildPartyKey(partyId), "leader");
    }

    public Set<Response<String>> getLeaders(Collection<Integer> partyIds, Pipeline pipeline) {
        return partyIds.stream().map((partyId) -> pipeline.hget(buildPartyKey(partyId), "leader")).collect(Collectors.toSet());
    }

    //
    
    public Response<Set<String>> getMembers(Integer partyId, Pipeline pipeline) {
        return pipeline.smembers(buildMembersKey(partyId));
    }

    // Invitations

    public Response<Set<String>> getInvitations(Integer userId, Pipeline pipeline) {
        return pipeline.smembers(buildInvitationsKey(userId));
    }

    public Boolean hasInvitation(Integer userId, Integer partyId) {
        try (Jedis jedis = this.provider.provide().getResource()) {
            Pipeline pipeline = jedis.pipelined();
            Response<Boolean> response = hasInvitation(userId, partyId, pipeline);
            pipeline.sync();

            return response.get();
        }
    }

    public Response<Boolean> hasInvitation(Integer userId, Integer partyId, Pipeline pipeline) {
        return pipeline.sismember(buildInvitationsKey(userId), partyId.toString());
    }

    // User Party
    public Integer getParty(@NonNull Integer userId) {
        try (Jedis jedis = this.provider.provide().getResource()) {
            String str = jedis.hget(buildUserPartyKey( userId), "party");
            return str == null ? null : Ints.tryParse(str);
        }
    }

    public Response<String> getParty(@NonNull Integer userId, @NonNull Pipeline pipeline) {
        return pipeline.hget(buildUserPartyKey(userId), "party");
    }

}
