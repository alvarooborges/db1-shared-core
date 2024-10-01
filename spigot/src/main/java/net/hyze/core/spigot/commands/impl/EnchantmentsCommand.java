package net.hyze.core.spigot.commands.impl;

import com.google.common.base.Enums;
import com.google.common.collect.Lists;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantment;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentRegistry;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentSlot;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.PlayerNMS;
import java.util.Arrays;
import java.util.List;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftMetaBook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class EnchantmentsCommand extends CustomCommand {

    public EnchantmentsCommand() {
        super("encantamentos", CommandRestriction.IN_GAME, "encantar");
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        String target = args.length > 0 ? args[0] : null;

        CustomEnchantmentSlot slot;
        CustomEnchantment enchantment;

        if (target == null) {
            openMainBook((Player) sender);
        } else if ((slot = Enums.getIfPresent(CustomEnchantmentSlot.class, target).orNull()) != null) {
            openSlotBook((Player) sender, slot);
        } else if ((enchantment = CustomEnchantmentRegistry.get(target)) != null) {
            openEnchantmentBook((Player) sender, enchantment);
        } else {
            Message.ERROR.send(sender, "Nada para mostrar.\nUse /encantamentos");
            return;
        }
    }

    private void openBook(Player player, List<IChatBaseComponent> pages) {

        ItemStack bookItemStack = new ItemStack(Material.WRITTEN_BOOK);

        BookMeta bookMeta = (BookMeta) bookItemStack.getItemMeta();
        bookMeta.setTitle("...");
        bookMeta.setAuthor("...");

        CraftMetaBook craftMetaBook = (CraftMetaBook) bookMeta;

        craftMetaBook.pages = pages;

        bookItemStack.setItemMeta(bookMeta);

        PlayerNMS.openBook(player, bookItemStack);
    }

    private void openMainBook(Player player) {
        List<IChatBaseComponent> pages = Lists.newArrayList();

        ComponentBuilder page = new ComponentBuilder("Clique na categoria: \n\n");

        for (CustomEnchantmentSlot slot : CustomEnchantmentSlot.values()) {
            page.append(slot.getName(), ComponentBuilder.FormatRetention.ALL)
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/encantamentos " + slot.name()))
                    .append("\n");
        }

        pages.add(IChatBaseComponent.ChatSerializer.a(ComponentSerializer.toString(new TextComponent(page.create()))));

        openBook(player, pages);
    }

    private void openSlotBook(Player player, CustomEnchantmentSlot stot) {
        List<IChatBaseComponent> pages = Lists.newArrayList();

        ComponentBuilder page = new ComponentBuilder("Clique no encantamento: \n\n");

        for (CustomEnchantment enchantment : CustomEnchantmentRegistry.getItems()) {
            if (Arrays.asList(enchantment.getSlots()).contains(stot)) {
                page.append(enchantment.getDisplayName(), ComponentBuilder.FormatRetention.ALL)
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/encantamentos " + enchantment.getKey()))
                        .append("\n");
            }
        }

        pages.add(IChatBaseComponent.ChatSerializer.a(ComponentSerializer.toString(new TextComponent(page.create()))));

        openBook(player, pages);
    }

    private void openEnchantmentBook(Player player, CustomEnchantment enchantment) {
        List<IChatBaseComponent> pages = Lists.newArrayList();

        ComponentBuilder page = new ComponentBuilder(enchantment.getDisplayName() + ": \n");

        for (String desc : enchantment.getDescription()) {
            page.append(desc);
        }

        pages.add(IChatBaseComponent.ChatSerializer.a(ComponentSerializer.toString(new TextComponent(page.create()))));

        openBook(player, pages);
    }

}
