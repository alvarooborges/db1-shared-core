package net.hyze.core.spigot.misc.report;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.echo.packets.BroadcastMessagePacket;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.misc.report.Report;
import net.hyze.core.shared.misc.report.ReportCategory;
import net.hyze.core.shared.misc.report.ReportItem;
import net.hyze.core.shared.misc.report.ReportManager;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AutoReport {

    public static final ConcurrentMap<String, Integer> COUNTS = Maps.newConcurrentMap();

    public static int VALUE = 5;

    public static Supplier<Integer> getTPS;

    public static void breakBlock(Player player) {
        COUNTS.put(player.getName(), COUNTS.getOrDefault(player.getName(), 0) + 1);
    }

    public static int get(Player player) {
        return COUNTS.getOrDefault(player.getName(), 0);
    }

    public static void reset(Player player) {
        COUNTS.remove(player.getName());
    }

    public static void report(Player player, int value) {

        User user = CoreProvider.Cache.Local.USERS.provide().get(player.getName());

        Report report = CoreProvider.Cache.Redis.REPORTS.provide().getReport(user);
        ReportCategory category = ReportManager.getCategory("Fast Break");

        if (category != null) {
            // Remove o report automático caso exista
            // report.getReports(Common.SERVER.getId())
            // .removeIf(ri -> ri.getCategory().equals(category) && ri.getReporter() == 0);

            ReportItem reportItem = new ReportItem(0, category, CoreProvider.getApp().getId());
            report.addReport(reportItem);

            // Insere um report automático novo
            CoreProvider.Cache.Redis.REPORTS.provide().updateReport(report);
        } else {
            System.out.println("ERRO: Falha ao obter categoria 'Fast Break'!");
        }

        ComponentBuilder hover = new ComponentBuilder("Fast Break\n")
                .color(ChatColor.GRAY)
                .append(player.getName() + " quebrou " + value + " blocos no último segundo.\n\n")
                .append("Clique para ir até o jogador")
                .color(ChatColor.YELLOW);

        ComponentBuilder builder = new ComponentBuilder("\u26A0 " + player.getName())
                .color(ChatColor.RED)
                .bold(true)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover.create()))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + player.getName()));

        BroadcastMessagePacket messagePacket = BroadcastMessagePacket.builder()
                .components(builder.create())
                .groups(Collections.singleton(Group.MANAGER))
                .build();

        CoreProvider.Redis.ECHO.provide().publish(messagePacket);

        String playerName = player.getName();

        if (value > 5) {
            Bukkit.getScheduler().runTask(CoreSpigotPlugin.getInstance(), () -> {

                Player player0 = Bukkit.getPlayerExact(playerName);

                if (player0 == null) {
                    return;
                }

                ItemStack item = player0.getItemInHand();

                if (item != null && item.getEnchantments().containsKey(Enchantment.DIG_SPEED) && item.getEnchantmentLevel(Enchantment.DIG_SPEED) == 6) {
                    return;
                }

                player0.kickPlayer(ChatColor.RED + "Você foi desconectado!");

            });
        }

    }

    public static int getValue() {
        int tps = AutoReport.getTPS == null ? 20 : AutoReport.getTPS.get();
        return VALUE + (tps < 17 ? 3 : 0);
    }
}
