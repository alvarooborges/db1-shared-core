package net.hyze.core.spigot.commands.impl;

import lombok.Getter;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.inventory.PaginateInventory;
import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.customitem.CustomItemRegistry;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomItemCommand extends CustomCommand implements GroupCommandRestrictable {

    @Getter
    private final Group group = Group.GAME_MASTER;

    public CustomItemCommand() {
        super("customitem", CommandRestriction.IN_GAME, "citem");
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        Player player = (Player) sender;

        PaginateInventory.PaginateInventoryBuilder inventory = PaginateInventory.builder();

        for (CustomItem item : CustomItemRegistry.getItems()) {
            inventory.item(ItemBuilder.of(item.asItemStack()).lore("", "&fid: &7" + item.getKey()).make(), event -> {
                ItemStack stack = item.asItemStack();

                if (!InventoryUtils.fits(player.getInventory(), stack)) {
                    Message.ERROR.send(player, "Seu inventário está cheio.");
                    return;
                }

                player.getInventory().addItem(stack);

                Message.SUCCESS.send(player, String.format("Você recebeu 1 %s&a.", item.getDisplayName()));
            });
        }
        player.openInventory(inventory.build("Itens Customizados"));
    }
}
