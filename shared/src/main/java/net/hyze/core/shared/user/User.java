package net.hyze.core.shared.user;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.primitives.Ints;
import java.util.Date;
import java.util.EnumSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.misc.utils.BCrypt;

@Getter
@ToString(of = {"id", "nick", "email", "emailVerifiedAt", "createdAt"})
@EqualsAndHashCode(of = "id")
public class User implements Credentialable {

    private final static Cache<User, Boolean> LOGGED_CACHE = Caffeine.newBuilder()
            .expireAfterWrite(3, TimeUnit.SECONDS)
            .build();

    private final static Cache<User, Integer> CASH_CACHE = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.SECONDS)
            .build();

    private final Integer id;

    private final String nick;

    @Setter
    private UUID uuid;

    @Setter
    private String password;

    private final String email;

    private final Date emailVerifiedAt;

    private final Date createdAt;

    public User(Integer id, String nick, UUID uuid, String password, String email, Date emailVerifiedAt, Date createAt) {
        this.id = id;
        this.nick = nick;
        this.uuid = uuid;
        this.password = password;
        this.email = email;
        this.createdAt = createAt;
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public boolean attemptLogin(String plainPassword) {
        return BCrypt.checkpw(plainPassword, this.password);
    }

    public boolean hasStrictGroup(Group group) {
        return group.equals(Group.DEFAULT) || this.getGroups().contains(group);
    }

    public EnumSet<Group> getGroups() {
        return CoreProvider.Cache.Local.USERS_GROUPS.provide().get(this);
    }

    public boolean hasGroup(Group group) {
        Group highest = getHighestGroup();

        return !group.isHigher(highest);
    }

    public Group getHighestGroup() {
        EnumSet<Group> groups = this.getGroups();

        return groups.stream()
                .min((Group o1, Group o2) -> Ints.compare(o2.getPriority(), o1.getPriority()))
                .orElse(Group.DEFAULT);
    }

    public boolean hasEmail() {
        return this.email != null;
    }

    public boolean hasUuid() {
        return this.uuid != null;
    }

    public boolean hasVerifiedEmail() {
        return this.emailVerifiedAt != null && this.email != null;
    }

    public void setLogged(boolean value) {
        LOGGED_CACHE.put(this, value);
    }

    public boolean isLogged() {
        return LOGGED_CACHE.get(this, user -> {
            return CoreProvider.Cache.Redis.USERS_STATUS.provide().isLogged(user.getNick());
        });
    }

    /**
     * Use esse valor apenas para estética.
     * <p>
     * Essa função retorna o cash do jogador em CACHE de 1 segundo.
     * <p>
     * Para ter certeza do saldo use o getRealCash()
     *
     * @return
     */
    public int getCash() {
        return CASH_CACHE.get(this, user -> {
            return CoreProvider.Repositories.USERS.provide().fetchCash(user);
        });
    }

    public int getRealCash() {
        return CoreProvider.Repositories.USERS.provide().fetchCash(this);
    }

    public boolean incrementCash(int value) {
        boolean success = CoreProvider.Repositories.USERS.provide().incrementCash(this, value);

        if (!success) {
            return false;
        }

        Integer cached = CASH_CACHE.getIfPresent(this);

        if (cached != null) {
            CASH_CACHE.put(this, cached + value);
        }

        return true;
    }

    public boolean decrementCash(int value) {
        boolean success = CoreProvider.Repositories.USERS.provide().decrementCash(this, value);

        if (!success) {
            return false;
        }

        Integer cached = CASH_CACHE.getIfPresent(this);

        if (cached != null) {
            CASH_CACHE.put(this, cached - value);
        }

        return true;
    }

    public boolean defineCash(int value) {
        boolean success = CoreProvider.Repositories.USERS.provide().defineCash(this, value);

        if (!success) {
            return false;
        }

        CASH_CACHE.put(this, value);

        return true;
    }
}
