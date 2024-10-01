package net.hyze.core.spigot.commands.impl;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import net.hyze.core.shared.commands.Argument;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.commands.arguments.NickArgument;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GiveCommand extends CustomCommand implements GroupCommandRestrictable {

    private static List<String> materials;

    static {
        ArrayList<String> materialList = new ArrayList<String>();
        for (Material material : Material.values()) {
            materialList.add(material.name());
        }
        Collections.sort(materialList);
        materials = ImmutableList.copyOf(materialList);
    }

    public GiveCommand() {
        super("give", CommandRestriction.CONSOLE_AND_IN_GAME);

        registerArgument(new NickArgument("nick", "", true));
        registerArgument(new Argument("item", "", true));
        registerArgument(new Argument("amount|data", "", false));
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }


    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        Player player = Bukkit.getPlayerExact(args[0]);

        if (player == null) {
            sender.sendMessage("Can't find player " + args[0]);
            return;
        }

        Material material = Material.matchMaterial(args[1]);

        if (material == null) {
            material = Bukkit.getUnsafe().getMaterialFromInternalName(args[1]);
        }

        if (material == null) {
            sender.sendMessage("There's no item called " + args[1]);
            return;
        }

        int amount = 1;
        short data = 0;

        if (args.length >= 3) {
            amount = this.getInteger(sender, args[2], 1, 64);

            if (args.length >= 4) {
                try {
                    data = Short.parseShort(args[3]);
                } catch (NumberFormatException ex) {
                }
            }
        }

        ItemStack stack = new ItemStack(material, amount, data);

        if (args.length >= 5) {
            try {
                stack = Bukkit.getUnsafe().modifyItemStack(stack, Joiner.on(' ').join(Arrays.asList(args).subList(4, args.length)));
            } catch (Throwable t) {
                player.sendMessage("Not a valid tag");
                return;
            }
        }

        player.getInventory().addItem(stack);

        Command.broadcastCommandMessage(sender, "Gave " + player.getName() + " some " + material.getId() + " (" + material + ")");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        if (args.length == 1) {
            return super.tabComplete(sender, alias, args);
        }
        if (args.length == 2) {
            final String arg = args[1];
            final List<String> materials = GiveCommand.materials;
            List<String> completion = new ArrayList<String>();

            final int size = materials.size();
            int i = Collections.binarySearch(materials, arg, String.CASE_INSENSITIVE_ORDER);

            if (i < 0) {
                // Insertion (start) index
                i = -1 - i;
            }

            for (; i < size; i++) {
                String material = materials.get(i);
                if (StringUtil.startsWithIgnoreCase(material, arg)) {
                    completion.add(material);
                } else {
                    break;
                }
            }

            return Bukkit.getUnsafe().tabCompleteInternalMaterialName(arg, completion);
        }
        return ImmutableList.of();
    }
}
