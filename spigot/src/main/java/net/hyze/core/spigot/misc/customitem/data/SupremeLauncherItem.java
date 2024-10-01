package net.hyze.core.spigot.misc.customitem.data;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.misc.cooldowns.UserCooldowns;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.CoreSpigotConstants;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.customitem.CustomItemRegistry;
import net.hyze.core.spigot.misc.customitem.StickyCustomItem;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.greenrobot.eventbus.Subscribe;

public class SupremeLauncherItem extends CustomItem implements StickyCustomItem {

    @Getter
    private final ItemBuilder itemBuilder;

    private final int MAX_USAGE = 20;

    private final long REGEN_TIME = TimeUnit.MINUTES.toMillis(5);

    public final String NBT_AMOUNT_KEY = "supreme_launcher_amount";
    public final String NBT_LAST_REGEN_KEY = "supreme_launcher_last_regen";

    public SupremeLauncherItem() {
        super("custom_item_supreme_laucher");

        this.itemBuilder = ItemBuilder.of(Material.FIREWORK)
                .glowing(true)
                .name("&cLançador Supremo")
                .lore(
                        "Utilize este item para ser lançado em",
                        "40 blocos para cima!",
                        "",
                        "Ao utilizar este item, uma carga é",
                        "consumida. A cada 5 minutos uma",
                        "carga é restaurada.",
                        "",
                        "Este item não é dropado ao morrer."
                );
    }

    @Override
    public ItemStack asItemStack(int amount) {
        ItemStack stack = ItemBuilder.of(super.asItemStack(amount), true)
                .nbt(NBT_AMOUNT_KEY, 20)
                .nbt(NBT_LAST_REGEN_KEY, System.currentTimeMillis())
                .make();

        update(stack);

        return stack;
    }

    @Override
    public String getDisplayName() {
        return "&cLançador Supremo";
    }

    private void update(ItemStack item) {
        ItemBuilder builder = ItemBuilder.of(item, true);

        if (CustomItemRegistry.getByItemStack(item) != this
                || !builder.hasNbt(NBT_AMOUNT_KEY)) {
            return;
        }

        long amount = Math.max(builder.nbtInt(NBT_AMOUNT_KEY), 0);

        if (amount < MAX_USAGE) {
            if (builder.hasNbt(NBT_LAST_REGEN_KEY)) {
                long lastUpdate = builder.nbtLong(NBT_LAST_REGEN_KEY);

                long diff = System.currentTimeMillis() - lastUpdate;

                long regenAmount = diff / REGEN_TIME;

                if (regenAmount > 0) {
                    amount = Math.min(amount + regenAmount, MAX_USAGE);

                    long offset = diff - (regenAmount * REGEN_TIME);

                    builder.nbt(NBT_LAST_REGEN_KEY, System.currentTimeMillis() - offset);
                }
            } else {
                builder.nbt(NBT_LAST_REGEN_KEY, System.currentTimeMillis());
            }
        }

        if (amount >= MAX_USAGE) {
            builder.removeNbt(NBT_LAST_REGEN_KEY);
        }

        builder.nbt(NBT_AMOUNT_KEY, amount);

        builder.name(String.format(
                "%s %s(%s/%s)",
                getDisplayName(),
                amount == 0 ? "&4" : "&a",
                amount,
                MAX_USAGE
        ));
    }

    @Subscribe
    public void on(PlayerPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        if (CustomItemRegistry.getByItemStack(item) != this) {
            return;
        }

        update(item);
    }

    @Subscribe
    public void on(PlayerItemHeldEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItem(event.getNewSlot());

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        if (CustomItemRegistry.getByItemStack(item) != this) {
            return;
        }

        update(item);
    }

    @Subscribe
    public void on(InventoryClickEvent event) {

        CURRENT_ITEM:
        {
            ItemStack item = event.getCurrentItem();
            if (item == null || item.getType() == Material.AIR) {
                break CURRENT_ITEM;
            }

            if (CustomItemRegistry.getByItemStack(item) != this) {
                break CURRENT_ITEM;
            }

            update(item);
        }

        CURSOR_ITEM:
        {
            ItemStack item = event.getCursor();
            if (item == null || item.getType() == Material.AIR) {
                break CURSOR_ITEM;
            }

            if (CustomItemRegistry.getByItemStack(item) != this) {
                break CURSOR_ITEM;
            }

            update(item);
        }
    }

    @Subscribe
    public void on(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        event.setCancelled(true);

        ItemBuilder item = ItemBuilder.of(event.getItem(), true);

        if (!item.hasNbt(NBT_AMOUNT_KEY)) {
            return;
        }

        int amount = item.nbtInt(NBT_AMOUNT_KEY);
        int newAmount = amount - 1;

        item.nbt(NBT_AMOUNT_KEY, Math.max(newAmount, 0));
        update(event.getItem());

        if (amount <= 0) {
            return;
        }

        Player player = event.getPlayer();

        User user = CoreProvider.Cache.Local.USERS.provide().get(player.getName());

        if (!UserCooldowns.hasEnded(user, "user-launcher-item")) {
            return;
        }

        double up = 3.15;
        player.setVelocity(new Vector(0, up, 0));

        player.getWorld().playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 5, 0);

        Bukkit.getScheduler().runTaskAsynchronously(CoreSpigotPlugin.getInstance(), () -> {
            for (int i = 0; i < 200; i++) {
                player.getWorld().playEffect(player.getLocation(), Effect.FIREWORKS_SPARK, 1, 5);
            }
        });

        player.setMetadata(CoreSpigotConstants.NBTKeys.PLAYER_FALL_DAMAGE_BYPASS, new FixedMetadataValue(CoreSpigotPlugin.getInstance(), true));

        UserCooldowns.start(user, "user-launcher-item", 1, TimeUnit.SECONDS);
    }
}
