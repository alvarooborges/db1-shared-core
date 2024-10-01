package net.hyze.core.spigot.commands.impl;

import lombok.Getter;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.inventory.PaginateInventory;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantment;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentRegistry;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomEnchantmentCommand extends CustomCommand implements GroupCommandRestrictable {

    @Getter
    private final Group group = Group.GAME_MASTER;

    public CustomEnchantmentCommand() {
        super("customenchantment", CommandRestriction.IN_GAME, "cenchant");
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        Player player = (Player) sender;

        PaginateInventory.PaginateInventoryBuilder inventory = PaginateInventory.builder();

        for (CustomEnchantment enchantment : CustomEnchantmentRegistry.getItems()) {
            ItemBuilder builder = new ItemBuilder(Material.ENCHANTED_BOOK)
                    .name("&e" + enchantment.getDisplayName())
                    .lore(enchantment.getDescription());

            inventory.item(builder.make(), event -> {
                PaginateInventory.PaginateInventoryBuilder sub = PaginateInventory.builder();

                for (int i = 1; i <= enchantment.getMaxLevel(); i++) {
                    ItemStack book = enchantment.getBook(i, 1);
                    String name = enchantment.getDisplayName(i);
                    sub.item(book, subEvent -> {
                        if (!InventoryUtils.fits(player.getInventory(), book)) {
                            Message.ERROR.send(player, "Seu inventário está cheio.");
                            return;
                        }

                        player.getInventory().addItem(book);

                        Message.SUCCESS.send(player, String.format("Você recebeu 1 livro %s.", name));
                    });
                }

                sub.backInventory(() -> inventory.build("Encantamento Customizados"));

                player.openInventory(sub.build(enchantment.getDisplayName()));
            });
        }

        player.openInventory(inventory.build("Encantamentos Customizados"));
    }
}
