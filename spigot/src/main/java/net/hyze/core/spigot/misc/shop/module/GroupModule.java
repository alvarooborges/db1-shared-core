package net.hyze.core.spigot.misc.shop.module;

import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GroupModule extends AbstractModule {

    @Getter
    private final Group group;

    @Override
    public State state(User user) {
        return user.hasGroup(group) ? State.SUCCESS : State.ERROR;
    }

    public String[] addLore(User user, State state) {

        if (group != null) {
            return new String[]{
                "&cVocê precisa do grupo " + group.getDisplayName() + "&c."
            };
        }

        return null;
    }
}
