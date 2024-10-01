package net.hyze.core.spigot.misc.combat;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.echo.packets.UserCombatEnterPacket;
import net.hyze.core.spigot.echo.packets.UserCombatLeavePacket;
import net.hyze.core.spigot.misc.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CombatManager {

    protected static final ConcurrentMap<Integer, Combat> COMBATS = Maps.newConcurrentMap();

    protected static final Multimap<Integer, Integer> COMBATS_TARGETS = Multimaps.synchronizedSetMultimap(HashMultimap.create());

    public static final Set<String> ALLOWED_COMMANDS = Sets.newHashSet(
            "g", ".", "a", "tell", "r"
    );

    @Getter
    @Setter
    private static int combatTime = 15;

    @Getter
    @Setter
    private static Function<User, String> nameParser = user -> {
        return user.getNick();
    };

    /**
     * Utilizado exclusivamente para a guerra. :S
     *
     * @param user
     */
    public static void tag(@NonNull User user) {
        COMBATS.put(user.getId(), new Combat(Bukkit.getScheduler().runTaskLater(CoreSpigotPlugin.getInstance(), () -> {
        }, 20L)));
    }

    public static void tag(@NonNull User user, int seconds) {
        if (COMBATS.containsKey(user.getId())) {
            COMBATS.get(user.getId()).cancelTask();
        }

        COMBATS.put(user.getId(), new Combat(Bukkit.getScheduler().runTaskLater(CoreSpigotPlugin.getInstance(), () -> {
            untag(user);

            Player player = Bukkit.getPlayerExact(user.getNick());

            if (player.isOnline()) {
                Message.SUCCESS.send(player, "Você &lnão &aestá mais em combate, caso queira, pode deslogar sem perigo.");
            }

            CoreProvider.Redis.ECHO.provide().publish(new UserCombatLeavePacket(user));
        }, ((long) seconds) * 20L)));
    }

    public static void tag(@NonNull User user, @NonNull User opponent) {

        Player player = Bukkit.getPlayerExact(user.getNick());

        if (!COMBATS.containsKey(user.getId())) {
            COMBATS_TARGETS.put(user.getId(), opponent.getId());

            Message.EMPTY.send(player, String.format(
                    "&cVocê entrou em combate com %s&c, aguarde %d segundos para deslogar.",
                    getNameParser().apply(opponent),
                    getCombatTime()
            ));

            CoreProvider.Redis.ECHO.provide().publish(new UserCombatEnterPacket(user, opponent));
        } else {
            COMBATS_TARGETS.put(user.getId(), opponent.getId());
            COMBATS.get(user.getId()).cancelTask();
        }

        COMBATS.put(user.getId(), new Combat(Bukkit.getScheduler().runTaskLater(CoreSpigotPlugin.getInstance(), () -> {
            untag(user);

            if (player.isOnline()) {
                Message.SUCCESS.send(player, "Você &lnão &aestá mais em combate, caso queira, pode deslogar sem perigo.");
            }

            CoreProvider.Redis.ECHO.provide().publish(new UserCombatLeavePacket(user));
        }, getCombatTime() * 20L)));
    }

    public static void untag(@NonNull User user) {
        Combat combat = COMBATS.remove(user.getId());

        if (combat != null) {
            combat.cancelTask();
        }

        COMBATS_TARGETS.removeAll(user.getId());

        Multiset<Integer> keys = ImmutableMultiset.copyOf(COMBATS_TARGETS.keys());

        for (Integer key : keys) {
            COMBATS_TARGETS.remove(key, user.getId());
        }
    }

    public static boolean isTagged(@NonNull User user) {
        Combat combat = COMBATS.get(user.getId());
        if (combat == null) {
            return false;
        }

        if (combat.hasEnded()) {
            untag(user);
            return false;
        } else {
            return true;
        }
    }

    public static Collection<Integer> getOpponents(@NonNull User user) {
        return COMBATS_TARGETS.get(user.getId());
    }
}
