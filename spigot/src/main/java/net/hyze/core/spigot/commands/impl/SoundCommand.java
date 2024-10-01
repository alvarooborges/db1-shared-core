package net.hyze.core.spigot.commands.impl;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.inventory.PaginateInventory;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;

public class SoundCommand extends CustomCommand implements GroupCommandRestrictable {

    public SoundCommand() {
        super("sound", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        Player player = (Player) sender;
        player.openInventory(new SoundInventory(player));
    }

    @Override
    public Group getGroup() {
        return Group.ADMINISTRATOR;
    }

    private class SoundInventory extends PaginateInventory {

        public SoundInventory(Player player) {
            super("Sons");
            
            Location location = player.getLocation();

            for (Sound sound : Sound.values()) {
                ItemBuilder builder = new ItemBuilder(Material.RECORD_4)
                        .name("&e" + sound.name());

                addItem(builder.make(), event -> {

                    if (event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                        player.playSound(location, sound, 10, 1.0f);
                        return;
                    }

                    if (event.getAction().equals(InventoryAction.PICKUP_HALF)) {
                        player.openInventory(new PlayInventory(player, sound, 1.0f));
                    }

                });
            }
        }

    }

    private class PlayInventory extends CustomInventory {

        public PlayInventory(Player player, Sound sound, float value) {
            super(9 * 4, String.format("Sons - %s - %.1f", sound.name(), value));

            ItemBuilder item0 = new ItemBuilder(Material.STAINED_GLASS_PANE)
                    .durability(14)
                    .name("&c-");

            ItemBuilder item1 = new ItemBuilder(Material.RECORD_3)
                    .name("&e" + sound.name())
                    .lore("&eValor: &f" + value);

            ItemBuilder item2 = new ItemBuilder(Material.STAINED_GLASS_PANE)
                    .durability(5)
                    .name("&a+");

            setItem(
                    12,
                    item0.make(),
                    event -> {
                        if ((value - 0.1000f) < 0f) {
                            player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 0);
                            return;
                        }

                        player.openInventory(new PlayInventory(player, sound, value - 0.1000f));
                        player.playSound(player.getLocation(), Sound.CLICK, 10, 1);
                    }
            );

            setItem(
                    13,
                    item1.make(),
                    event -> {
                        player.playSound(player.getLocation(), sound, 10, value);
                    }
            );

            setItem(
                    14,
                    item2.make(),
                    event -> {
                        if ((value + 0.1000f) > 2f) {
                            player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 0);
                            return;
                        }

                        player.openInventory(new PlayInventory(player, sound, value + 0.1000f));
                        player.playSound(player.getLocation(), Sound.CLICK, 10, 1);
                    }
            );
            
            backItem(new SoundInventory(player));
        }

    }
}
