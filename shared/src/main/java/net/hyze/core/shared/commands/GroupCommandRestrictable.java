package net.hyze.core.shared.commands;

import lombok.NonNull;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;

public interface GroupCommandRestrictable extends CommandRestrictable {

    Group getGroup();

    /**
     * Se o comando é restrito somente para o grupo do getGroup().
     * <p>
     * Por default o comando é tambem habilitado para todos os outros grupos
     * superiores.
     */
    default public boolean strict() {
        return false;
    }

    @Override
    default public boolean canExecute(@NonNull User user) {


        if (strict()) {
            return user.hasStrictGroup(this.getGroup());
        }

        return user.hasGroup(this.getGroup());
    }

    @Override
    default public String getErrorMessage() {
        return String.format("Você precisa do grupo %s ou superior para executar este comando.", getGroup().getDisplayNameStriped());
    }

}
