package net.hyze.core.spigot.commands.impl.cash;

import lombok.Getter;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.commands.impl.cash.subcommands.AddCashCommand;

public class CashCommand extends CustomCommand implements GroupCommandRestrictable {

    @Getter
    private final Group group = Group.MANAGER;

    public CashCommand() {
        super("cash");

        registerSubCommand(new AddCashCommand());
    }
}
