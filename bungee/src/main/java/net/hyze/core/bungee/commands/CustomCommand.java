package net.hyze.core.bungee.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.hyze.core.shared.commands.Argument;
import net.hyze.core.shared.commands.CommandRestriction;

import java.util.Arrays;
import java.util.Set;

import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.command.ConsoleCommandSender;
import net.hyze.core.shared.commands.Commandable;

import java.util.LinkedList;
import java.util.Map;

public class CustomCommand extends Command implements Commandable<CommandSender>, TabExecutor {

    @Getter
    protected final String name0;

    @Getter
    protected final Set<String> aliases0;

    @Getter
    private final CommandRestriction commandRestriction;

    @Getter
    private final Map<CustomCommand, String> subCommands = Maps.newConcurrentMap();

    @Getter
    private final LinkedList<Argument> arguments = Lists.newLinkedList();

    public CustomCommand(String name, CommandRestriction commandRestriction, String... aliases) {
        super(name, null, aliases);
        this.name0 = name;
        this.aliases0 = Sets.newHashSet(aliases);
        this.commandRestriction = commandRestriction;
    }

    public CustomCommand(String name, String... aliases) {
        this(name, CommandRestriction.CONSOLE_AND_IN_GAME, aliases);
    }

    @Override
    public boolean isConsole(CommandSender sender) {
        return sender instanceof ConsoleCommandSender;
    }

    @Override
    public boolean isPlayer(CommandSender sender) {
        return sender instanceof ProxiedPlayer;
    }

    @Override
    public String getSenderNick(CommandSender sender) {
        return sender.getName();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        this.executeRaw(sender, name0, args);
    }

    public final void registerSubCommand(CustomCommand subCommand) {
        this.registerSubCommand(subCommand, new String());
    }

    public final void registerSubCommand(CustomCommand subCommand, String description) {
        this.subCommands.put(subCommand, description);
    }

    public final void registerArgument(Argument argument) {
        arguments.add(argument);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        CustomCommand subCommand = getSubCommands().keySet()
                .stream()
                .filter(sub -> sub.getName0().equalsIgnoreCase(args[0]) || sub.getAliases0().stream().anyMatch(a -> a.equalsIgnoreCase(args[0])))
                .findFirst()
                .orElse(null);

        if (subCommand != null) {
            return subCommand.onTabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
        }

        return this.tabComplete0(sender, this.getName0(), args);
    }
}
