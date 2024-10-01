package net.hyze.core.spigot.commands.impl.basics;

import net.hyze.core.shared.commands.Argument;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class EnchantCommand extends CustomCommand implements GroupCommandRestrictable {

    public EnchantCommand() {
        super("enchant", CommandRestriction.IN_GAME);
        
        registerArgument(new Argument("enchantment", "Nome do encantamento", true));
        registerArgument(new Argument("level", "Nível do encantamento"));
    }

    @Override
    public void onCommand(CommandSender sender, User requester, String[] args) {
        Player player = (Player) sender;

        Enchantment enchantment = null;

        int level = 1;

        try {
            enchantment = Enchantment.getById(Integer.parseInt(args[0]));
        } catch (Exception e) {

        }

        if (enchantment == null) {
            enchantment = Enchantment.getByName(args[0].toUpperCase());
        }

        if (enchantment == null) {

            Message.ERROR.send(player, "Encantamento não encontrado! Lista:");

            List<String> enchantments = new ArrayList<String>();

            for (Enchantment e : Enchantment.values()) {
                enchantments.add(ChatColor.DARK_AQUA + e.getName().toLowerCase());
            }

            String allEnchantments = StringUtils.join(enchantments, ChatColor.GRAY + ", " + ChatColor.RESET);

            player.sendMessage(allEnchantments + ChatColor.GRAY + ".");

            return;

        }

        if (args.length >= 2) {
            try {
                level = Integer.parseInt(args[1]);
            } catch (Exception e) {
                Message.ERROR.send(player, "O level '" + args[1] + " não existe!");
                return;
            }
        }

        ItemStack i = player.getItemInHand();

        if (i != null && i.getItemMeta() != null) {
            ItemMeta im = i.getItemMeta();

            if (i.getType().equals(Material.BOOK)) {
                i.setType(Material.ENCHANTED_BOOK);
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) i.getItemMeta();
                meta.addStoredEnchant(enchantment, level, true);

                i.setItemMeta(meta);
            } else {
                im.addEnchant(enchantment, level, true);
                i.setItemMeta(im);
            }

            player.setItemInHand(i);
            player.updateInventory();

            Message.SUCCESS.send(player, "Encantamento adicionado!");
        } else {
            Message.ERROR.send(player, "Este item não pode ser encantado.");
        }
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

}
