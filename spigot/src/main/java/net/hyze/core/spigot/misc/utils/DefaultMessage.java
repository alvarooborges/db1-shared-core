package net.hyze.core.spigot.misc.utils;

import net.hyze.core.spigot.misc.message.Message;
import java.util.function.BiConsumer;
import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public enum DefaultMessage {

    PLAYER_NOT_FOUND(
            (sender, arg) -> {
                Message.ERROR.send(sender, String.format("\n Ops! Aparentemente \"&7%s&c\" nunca entrou em nosso servidor.\n ", arg));

                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1, 1);
                }
            }
    ),
    NEGATIVE_NUMBER(
            (sender, arg) -> {
                Message.ERROR.send(sender, "\n &lBEEEH!&c Você inseriu um valor negativo.\n ");

                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1, 1);
                }
            }
    ),
    INVALID_NUMBER(
            (sender, arg) -> {
                Message.ERROR.send(sender, String.format("\n &lBEEEH!&c Nossos robôs não identificaram \"&7%s&c\" como um número.\n ", arg));

                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1, 1);
                }
            }
    );

    private final BiConsumer<CommandSender, String> consumer;

    public void send(CommandSender sender, String arg) {
        this.consumer.accept(sender, arg);
    }

}
