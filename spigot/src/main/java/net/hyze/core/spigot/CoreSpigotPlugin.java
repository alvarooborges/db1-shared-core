package net.hyze.core.spigot;

import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import net.hyze.client.protocol.ProtocolReference;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.echo.api.DebugPacket;
import net.hyze.core.shared.echo.api.Echo;
import net.hyze.core.shared.echo.packets.user.KickUserPacket;
import net.hyze.core.shared.misc.jackson.LocationDeserializer;
import net.hyze.core.shared.misc.report.ReportManager;
import net.hyze.core.shared.misc.utils.Printer;
import net.hyze.core.shared.user.User;
import net.hyze.core.shared.user.preferences.PreferenceStatus;
import net.hyze.core.spigot.client.ProtocolHandler;
import net.hyze.core.spigot.commands.CommandRegistry;
import net.hyze.core.spigot.commands.impl.AdminCommand;
import net.hyze.core.spigot.commands.impl.ChangePassCommand;
import net.hyze.core.spigot.commands.impl.ClearCommand;
import net.hyze.core.spigot.commands.impl.FireballCommand;
import net.hyze.core.spigot.commands.impl.FlyCommand;
import net.hyze.core.spigot.commands.impl.GameModeCommand;
import net.hyze.core.spigot.commands.impl.GiveCommand;
import net.hyze.core.spigot.commands.impl.HeadCommand;
import net.hyze.core.spigot.commands.impl.LocationCommand;
import net.hyze.core.spigot.commands.impl.NBTCommand;
import net.hyze.core.spigot.commands.impl.OnlineCommand;
import net.hyze.core.spigot.commands.impl.OpCommand;
import net.hyze.core.spigot.commands.impl.PluginCommand;
import net.hyze.core.spigot.commands.impl.PreferenceCommand;
import net.hyze.core.spigot.commands.impl.ShopTest;
import net.hyze.core.spigot.commands.impl.SoundCommand;
import net.hyze.core.spigot.commands.impl.TPCommand;
import net.hyze.core.spigot.commands.impl.TPHereCommand;
import net.hyze.core.spigot.commands.impl.TPPosCommand;
import net.hyze.core.spigot.commands.impl.TPWorldCommand;
import net.hyze.core.spigot.commands.impl.TitleClearCommand;
import net.hyze.core.spigot.commands.impl.basics.ColorsCommand;
import net.hyze.core.spigot.commands.impl.basics.EnchantCommand;
import net.hyze.core.spigot.commands.impl.basics.ExceptionCommand;
import net.hyze.core.spigot.commands.impl.basics.GodCommand;
import net.hyze.core.spigot.commands.impl.basics.HatCommand;
import net.hyze.core.spigot.commands.impl.basics.HealCommad;
import net.hyze.core.spigot.commands.impl.basics.InvseeCommand;
import net.hyze.core.spigot.commands.impl.basics.SignCommand;
import net.hyze.core.spigot.commands.impl.basics.SpeedCommand;
import net.hyze.core.spigot.commands.impl.basics.ThorCommand;
import net.hyze.core.spigot.commands.impl.basics.TpAllCommand;
import net.hyze.core.spigot.commands.impl.cash.CashCommand;
import net.hyze.core.spigot.commands.impl.group.GroupCommand;
import net.hyze.core.spigot.commands.impl.restart.RestartCommand;
import net.hyze.core.spigot.commands.impl.tell.ReplyCommand;
import net.hyze.core.spigot.commands.impl.tell.TellCommand;
import net.hyze.core.spigot.commands.impl.youtube.YoutubeCommand;
import net.hyze.core.spigot.echo.listeners.EchoListeners;
import net.hyze.core.spigot.echo.listeners.UserEchoListener;
import net.hyze.core.spigot.listeners.*;
import net.hyze.core.spigot.listeners.armor.ArmorListener;
import net.hyze.core.spigot.misc.customitem.listeners.CustomItemListener;
import net.hyze.core.spigot.misc.economy.CoreEconomy;
import net.hyze.core.spigot.misc.enchantments.listeners.CustomEnchantmentsListener;
import net.hyze.core.spigot.misc.enchantments.listeners.EnchantmentsMerchantRecipeListener;
import net.hyze.core.spigot.misc.hiddencuboid.HiddenCuboidManager;
import net.hyze.core.spigot.misc.hologram.HologramArmorStand;
import net.hyze.core.spigot.misc.jackson.MaterialDataDeserializer;
import net.hyze.core.spigot.misc.jackson.PotionEffectTypeDeserializer;
import net.hyze.core.spigot.misc.party.PartyManager;
import net.hyze.core.spigot.misc.preference.CorePreference;
import net.hyze.core.spigot.misc.preference.PreferenceIcon;
import net.hyze.core.spigot.misc.preference.PreferenceInventoryRegistry;
import net.hyze.core.spigot.misc.report.AutoReportTask;
import net.hyze.core.spigot.misc.scoreboard.protocol.listeners.ProtocolBoardPacketListener;
import net.hyze.core.spigot.misc.utils.CustomPayloadFixer;
import net.hyze.core.spigot.misc.utils.NMS;
import net.hyze.core.spigot.misc.utils.SignMenuFactory;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffectType;

public final class CoreSpigotPlugin extends CustomPlugin {

    @Getter
    private static CoreSpigotPlugin instance;

    @Getter
    private static SignMenuFactory signFactory;

    @Setter
    @Getter
    private static CoreEconomy economy;

    public CoreSpigotPlugin() {
        super(false);

        instance = this;

        CoreSpigotWrapper.setWrapper(new CoreSpigotWrapper());
    }

    @Override
    public void onEnable() {
        super.onEnable();

        // test.
        NMS.registerCustomEntity(HologramArmorStand.class, EntityArmorStand.class);

        SimpleModule module = new SimpleModule();
        module.addDeserializer(Location.class, new LocationDeserializer<>(Location.class, CoreSpigotConstants.LOCATION_PARSER));
        module.addDeserializer(PotionEffectType.class, new PotionEffectTypeDeserializer());
        module.addDeserializer(MaterialData.class, new MaterialDataDeserializer());
        CoreConstants.JACKSON.registerModule(module);

        ProtocolBoardPacketListener packetListener = new ProtocolBoardPacketListener(this);

        HiddenCuboidManager hiddenCuboidManager = new HiddenCuboidManager();

        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new InventoryListener(), this);
        pluginManager.registerEvents(new PlayerConnectionListener(), this);
        pluginManager.registerEvents(new EntityListener(), this);
        pluginManager.registerEvents(packetListener, this);
        pluginManager.registerEvents(new ArmorListener(), this);
        pluginManager.registerEvents(hiddenCuboidManager, this);

        pluginManager.registerEvents(new ServerListener(), this);
        pluginManager.registerEvents(new WorldListener(), this);
        pluginManager.registerEvents(new PlayerListener(), this);
        pluginManager.registerEvents(new CustomItemListener(), this);
        pluginManager.registerEvents(new EnchantmentsMerchantRecipeListener(), this);
        pluginManager.registerEvents(new CustomEnchantmentsListener(), this);

        /**
         * Registro de comandos.
         */
        CommandRegistry.registerCommand(new GodCommand());
        CommandRegistry.registerCommand(new YoutubeCommand());
        CommandRegistry.registerCommand(new LocationCommand());
        CommandRegistry.registerCommand(new FlyCommand());
        CommandRegistry.registerCommand(new GameModeCommand());
        CommandRegistry.registerCommand(new SpeedCommand());
        CommandRegistry.registerCommand(new PluginCommand());
        CommandRegistry.registerCommand(new HealCommad());
        CommandRegistry.registerCommand(new NBTCommand());
        CommandRegistry.registerCommand(new PreferenceCommand());
        CommandRegistry.registerCommand(new FireballCommand());
        CommandRegistry.registerCommand(new AdminCommand());
        CommandRegistry.registerCommand(new ChangePassCommand());
        CommandRegistry.registerCommand(new TellCommand());
        CommandRegistry.registerCommand(new ReplyCommand());
        CommandRegistry.registerCommand(new TPCommand());
        CommandRegistry.registerCommand(new TPPosCommand());
        CommandRegistry.registerCommand(new TPWorldCommand());
        CommandRegistry.registerCommand(new GroupCommand());
        CommandRegistry.registerCommand(new CashCommand());
        CommandRegistry.registerCommand(new ClearCommand());
        CommandRegistry.registerCommand(new EnchantCommand());
        CommandRegistry.registerCommand(new HatCommand());
        CommandRegistry.registerCommand(new InvseeCommand());
        CommandRegistry.registerCommand(new SignCommand());
        CommandRegistry.registerCommand(new ThorCommand());
        CommandRegistry.registerCommand(new TpAllCommand());
        CommandRegistry.registerCommand(new TitleClearCommand());
        CommandRegistry.registerCommand(new OnlineCommand());
        CommandRegistry.registerCommand(new TPHereCommand());
        CommandRegistry.registerCommand(new RestartCommand());
        CommandRegistry.registerCommand(new ColorsCommand());
        CommandRegistry.registerCommand(new OpCommand());
        CommandRegistry.registerCommand(new GiveCommand());
        CommandRegistry.registerCommand(new HeadCommand());
        CommandRegistry.registerCommand(new ExceptionCommand());
        CommandRegistry.registerCommand(new SoundCommand());
//        CommandRegistry.registerCommand(new EnchantmentsCommand());

        // TODO remover
        CommandRegistry.registerCommand(new ShopTest());

        /**
         * Registrando eventos do Echo
         */
        Echo echo = CoreProvider.Redis.ECHO.provide();

        Map<String, Timing> timings = Maps.newConcurrentMap();

        echo.subscribe((packet, runnable) -> {

            if (!CoreSpigotConstants.STOPPING) {

                Class clazz = packet.getClass();
                boolean debug = clazz.getAnnotation(DebugPacket.class) != null;

                if (debug) {
                    Printer.INFO.print(String.format("Executor - %s", clazz.getSimpleName()));
                }

                Runnable executor = () -> {
                    Timing timing = timings.getOrDefault(
                            packet.getClass().getName(),
                            Timings.of(
                                    CoreSpigotPlugin.this,
                                    "EchoPacket: " + packet.getClass().getName() + " (" + packet.getClass().getSimpleName() + ")"
                            )
                    );

                    timing.startTiming();
                    if (debug) {
                        Printer.INFO.print(String.format("Runnable run - %s", clazz.getSimpleName()));
                    }
                    runnable.run();
                    timing.stopTiming();
                };

                if (CoreSpigotPlugin.getInstance().getServer().isPrimaryThread()) {
                    if (debug) {
                        Printer.INFO.print(String.format("Primary Thread run - %s", clazz.getSimpleName()));
                    }
                    executor.run();
                } else {
                    Bukkit.getScheduler().runTask(CoreSpigotPlugin.this, () -> {
                        if (debug) {
                            Printer.INFO.print(String.format("RunTask run - %s", clazz.getSimpleName()));
                        }

                        executor.run();
                    });
                }
            }

        });

        echo.registerListener(new UserEchoListener());
        echo.registerListener(new EchoListeners());

        if (CoreProvider.getApp().getServer() != null) {
            echo.registerListener(new PartyManager());
        }

        /**
         * Registrando eventos do protocollib
         */
        ProtocolLibrary.getProtocolManager().addPacketListener(packetListener);

        // NÃO REMOVER!
        World world = Bukkit.getWorld("world");
        world.spawnEntity(world.getSpawnLocation(), EntityType.LIGHTNING);

        /**
         * Registrando preferência no inventário
         */
        PreferenceInventoryRegistry.registry(
                CorePreference.TELL.name(),
                new PreferenceIcon(
                        "Mensagens Privadas",
                        new String[]{"Habilite suas mensagens privadas."},
                        new ItemStack(Material.BOOK_AND_QUILL)
                ),
                PreferenceStatus.ON
        );

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, PacketType.Play.Client.CUSTOM_PAYLOAD) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                CustomPayloadFixer.checkCustomPayload(event);
            }
        });

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, PacketType.Play.Client.BLOCK_PLACE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                CustomPayloadFixer.checkPlace(event);
            }
        });

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            CustomPayloadFixer.PACKET_USAGE.keySet().removeIf(player -> !player.isOnline() || !player.isValid());
        }, 20L, 20L);

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            CustomPayloadFixer.PACKET_USAGE_PLACE.entrySet().stream()
                    .filter(entry -> entry.getValue() == -2)
                    .map(entry -> Bukkit.getPlayerExact(entry.getKey()))
                    .filter(Objects::nonNull)
                    .forEach(player -> {
                        User user = CoreProvider.Cache.Local.USERS.provide().get(player.getName());
                        KickUserPacket packet = new KickUserPacket(user, ChatColor.RED + "The books on the table");

                        CoreProvider.Redis.ECHO.provide().publish(packet);
                    });
        }, 20L, 20L);

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            CustomPayloadFixer.PACKET_USAGE_PLACE.keySet().removeIf(name -> Bukkit.getPlayerExact(name) == null);
        }, 20L, 20L);

        CoreProvider.Repositories.REPORTS.provide().fetchCategories().forEach(ReportManager::registerCategory);

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new AutoReportTask(), 20L, 20L);

        // Client Protocol
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, ProtocolReference.CHANNEL);
        this.getServer().getMessenger().registerIncomingPluginChannel(this, ProtocolReference.CHANNEL, (ProtocolHandler) CoreProvider.Client.PROTOCOL.provide());

        signFactory = new SignMenuFactory(this);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        Bukkit.getScheduler().cancelTasks(this);
//        Board.REGISTERED_BOARDS.forEach(Board::remove);
    }
}
