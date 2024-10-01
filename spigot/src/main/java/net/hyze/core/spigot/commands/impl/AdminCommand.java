package net.hyze.core.spigot.commands.impl;

import com.google.common.collect.Maps;
import com.google.common.primitives.Floats;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import net.hyze.client.protocol.packets.server.PacketAdvertisement;
import net.hyze.client.protocol.packets.server.PacketRequestScreenshot;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.Argument;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class AdminCommand extends CustomCommand implements GroupCommandRestrictable {

    public static final Map<UUID, Player> SCREENSHOT_CACHE= Maps.newHashMap();

    @Getter
    private final Group group = Group.GAME_MASTER;

    public AdminCommand() {
        super("admin", CommandRestriction.CONSOLE_AND_IN_GAME);

        registerSubCommand(new CustomCommand("cuboid", CommandRestriction.IN_GAME) {
            @Override
            public void onCommand(CommandSender sender, User user, String[] args) {
                Player player = (Player) sender;
                player.setMetadata("cuboid", new FixedMetadataValue(CoreSpigotPlugin.getInstance(), new CuboidSetup()));
                Message.SUCCESS.send(player, "Clique no primeiro canto seguido do segundo canto!");
            }
        });

        registerSubCommand(new CustomCommand("worlds") {
            @Override
            public void onCommand(CommandSender sender, User user, String[] args) {
                for(World world : Bukkit.getWorlds()) {
                    sender.sendMessage(world.getName());
                }
            }
        });

        registerSubCommand(new CustomCommand("ads", CommandRestriction.IN_GAME) {
            @Override
            public void onCommand(CommandSender sender, User user, String[] args) {
                Player player = (Player) sender;
                CoreProvider.Client.PROTOCOL.provide().sendPacket(player, new PacketAdvertisement(
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/7/77/Google_Images_2015_logo.svg/1200px-Google_Images_2015_logo.svg.png",
                        "https://google.com"
                ));

                Message.ERROR.send(sender, "Ads enviado");
            }
        });

        registerSubCommand(new CustomCommand("screen", CommandRestriction.IN_GAME) {
            {
                registerArgument(new Argument("quality", "Qualidade de imagem", false));
            }

            @Override
            public void onCommand(CommandSender sender, User user, String[] args) {
                float quality = args.length == 0 ? 1.0f : Floats.tryParse(args[0]);

                UUID id = UUID.randomUUID();

                Player player = (Player) sender;
                CoreProvider.Client.PROTOCOL.provide().sendPacket(player, new PacketRequestScreenshot(id, quality));
                SCREENSHOT_CACHE.put(id, player);

                Message.ERROR.send(sender, String.format("Screenshot requisitado com qualidade %d%%", (int) Math.ceil(quality * 100)));
            }
        });
    }

    @Setter
    @Getter
    public static class CuboidSetup {

        private Location first, second;
    }
}
