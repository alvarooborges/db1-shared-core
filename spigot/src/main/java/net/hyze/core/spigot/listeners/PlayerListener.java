package net.hyze.core.spigot.listeners;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.CommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.CoreSpigotConstants;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.commands.impl.AdminCommand;
import net.hyze.core.spigot.commands.impl.basics.GodCommand;
import net.hyze.core.spigot.misc.enchantments.merchant.MerchantUtil;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.core.spigot.misc.utils.WorldCuboid;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.Set;
import java.util.stream.Collectors;

public class PlayerListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void on(PlayerChatTabCompleteEvent event) {
        event.getTabCompletions().clear();

        if (event.getChatMessage().split(" ").length == 1) {
            if (event.getLastToken() != null && !event.getLastToken().isEmpty()) {
                String label = event.getLastToken().replaceAll("/", "");

                if (!label.isEmpty()) {
                    SimpleCommandMap commandMap = ((CraftServer) Bukkit.getServer()).getCommandMap();

                    User user = CoreProvider.Cache.Local.USERS.provide().get(event.getPlayer().getName());

                    Set<String> completions = commandMap.getCommands().stream()
                            .filter(cmd -> cmd instanceof CustomCommand)
                            .map(cmd -> (CustomCommand) cmd)
                            .filter(cmd -> {
                                if (cmd instanceof CommandRestrictable) {
                                    return ((CommandRestrictable) cmd).canExecute(user);
                                }

                                return true;
                            })
                            .filter(cmd -> StringUtil.startsWithIgnoreCase(cmd.getName(), label))
                            .map(cmd -> "/" + cmd.getName())
                            .collect(Collectors.toSet());

                    event.getTabCompletions().addAll(completions);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void on(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType() == EntityType.ITEM_FRAME) {
            event.setCancelled(!event.getPlayer().isOp());
        }
    }

    @EventHandler
    public void on(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerAchievementAwardedEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onLowest(PlayerInteractEvent event) {
        if (CoreSpigotConstants.STOPPING) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onLowest(EntityDamageEvent event) {
        if (CoreSpigotConstants.STOPPING) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onLowest(EntityDamageByEntityEvent event) {
        if (CoreSpigotConstants.STOPPING) {
            event.setCancelled(true);
            return;
        }

        if (event.getEntity() instanceof ItemFrame) {
            Entity damager = event.getDamager();

            if (damager instanceof Projectile) {
                Projectile projectile = (Projectile) damager;

                if (!(projectile.getShooter() instanceof Player)) {
                    event.setCancelled(true);
                    return;
                }

                damager = (Player) projectile.getShooter();
            }

            if (!(damager instanceof Player)) {
                event.setCancelled(true);
                return;
            }

            event.setCancelled(!((Player) damager).isOp());
            return;
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onLowest(PlayerPickupItemEvent event) {
        if (CoreSpigotConstants.STOPPING) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onLowest(PlayerDropItemEvent event) {
        if (CoreSpigotConstants.STOPPING) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onHighest(EntityDamageEvent event) {
        if (!(event instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (GodCommand.anyGod(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onHighest(BlockBreakEvent event) {
        User user = CoreProvider.Cache.Local.USERS.provide().get(event.getPlayer().getName());

        if (!user.hasGroup(Group.MANAGER) && GodCommand.anyGod(event.getPlayer())) {
            event.setCancelled(true);
            Message.ERROR.send(event.getPlayer(), "Você não pode fazer isso enquanto estiver no modo Deus.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onLowest(BlockBreakEvent event) {
        if (CoreSpigotConstants.STOPPING) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onLowest(BlockPlaceEvent event) {
        if (CoreSpigotConstants.STOPPING) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onHighest(BlockPlaceEvent event) {
        User user = CoreProvider.Cache.Local.USERS.provide().get(event.getPlayer().getName());

        if (!user.hasGroup(Group.MANAGER) && GodCommand.anyGod(event.getPlayer())) {
            event.setCancelled(true);
            Message.ERROR.send(event.getPlayer(), "Você não pode fazer isso enquanto estiver no modo Deus.");
        }
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void on(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        try {
            Block block = event.getClickedBlock();

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (player.hasMetadata("cuboid")) {
                    AdminCommand.CuboidSetup setup = (AdminCommand.CuboidSetup) player.getMetadata("cuboid").get(0).value();

                    if (setup.getFirst() == null) {
                        setup.setFirst(block.getLocation());
                        Message.SUCCESS.send(player, "Primeiro canto definido!");
                    } else {
                        setup.setSecond(block.getLocation());

                        WorldCuboid cuboid = new WorldCuboid(setup.getFirst(), setup.getSecond());
                        String json = CoreConstants.JACKSON.writeValueAsString(cuboid);

                        ComponentBuilder builder = new ComponentBuilder("Segundo canto definido! Clique").color(ChatColor.YELLOW)
                                .append(" AQUI").color(ChatColor.GREEN).bold(true)
                                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, json))
                                .append(" para copiar.").color(ChatColor.YELLOW);

                        player.sendMessage(builder.create());
                        player.removeMetadata("cuboid", CoreSpigotPlugin.getInstance());
                    }
                }
            }

            if (!event.isCancelled() && block.getType() == Material.ANVIL) {

                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    event.setCancelled(true);
                    MerchantUtil.openTrade(event.getPlayer());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            event.setCancelled(true);
        }

        if (event.getPlayer().isSneaking() && event.hasBlock() && event.getPlayer().isOp()) {

            String clickedBlockString = event.getClickedBlock().getType().name() + "(" + event.getClickedBlock().getData() + ")";

            ComponentBuilder builder = new ComponentBuilder(clickedBlockString)
                    .color(ChatColor.YELLOW)
                    .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, clickedBlockString));


            if (event.getPlayer().getItemInHand() != null) {
                String inHandString = event.getPlayer().getItemInHand().getData().toString();

                builder.append(" (", ComponentBuilder.FormatRetention.ALL)
                        .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, inHandString))
                        .color(ChatColor.GRAY)
                        .append(inHandString)
                        .append(")");
            }

            event.getPlayer().sendMessage(builder.create());
        }
    }

    private boolean tryRepairItemInHand(Player player) {
        ItemStack inHand = player.getItemInHand();

        if (inHand == null || inHand.getType() == Material.AIR) {
            return false;
        }

        net.minecraft.server.v1_8_R3.ItemStack nmsInHand = CraftItemStack.asNMSCopy(inHand.clone());
        net.minecraft.server.v1_8_R3.ItemStack nmsDiamond = CraftItemStack.asNMSCopy(new ItemStack(Material.DIAMOND));

        if (nmsInHand.e() && nmsInHand.getItem().a(nmsInHand, nmsDiamond)) {

            int k = Math.min(nmsInHand.h(), nmsInHand.j() / 7);

            if (k <= 0) {
                return false;
            }

            if (InventoryUtils.subtractOne(player, new ItemStack(Material.DIAMOND))) {
                int i1 = nmsInHand.h() - k;

                inHand.setDurability((short) i1);

                player.setItemInHand(inHand);
                return true;
            }
        }

        return false;
    }

}
