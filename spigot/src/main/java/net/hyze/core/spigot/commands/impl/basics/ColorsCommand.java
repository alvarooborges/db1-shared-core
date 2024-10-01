package net.hyze.core.spigot.commands.impl.basics;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ColorsCommand extends CustomCommand {

    public ColorsCommand() {
        super("cores", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User requester, String[] args) {
        Player player = (Player) sender;

        player.sendMessage(
                "\nCores disponíveis:"
                + "\n &0 " + MessageUtils.translateColorCodes("&0Preto")
                + ChatColor.WHITE
                + "\n &1 " + MessageUtils.translateColorCodes("&1Azul Escuro")
                + ChatColor.WHITE
                + "\n &2 " + MessageUtils.translateColorCodes("&2Verde Escuro")
                + ChatColor.WHITE
                + "\n &3 " + MessageUtils.translateColorCodes("&3Aqua Escuro")
                + ChatColor.WHITE
                + "\n &4 " + MessageUtils.translateColorCodes("&4Vermelho Escuro")
                + ChatColor.WHITE
                + "\n &5 " + MessageUtils.translateColorCodes("&5Roxo Escuro")
                + ChatColor.WHITE
                + "\n &6 " + MessageUtils.translateColorCodes("&6Laranja")
                + ChatColor.WHITE
                + "\n &7 " + MessageUtils.translateColorCodes("&7Cinza")
                + ChatColor.WHITE
                + "\n &8 " + MessageUtils.translateColorCodes("&8Cinza Escuro")
                + ChatColor.WHITE
                + "\n &9 " + MessageUtils.translateColorCodes("&9Azul")
                + ChatColor.WHITE
                + "\n &a " + MessageUtils.translateColorCodes("&aVerde")
                + ChatColor.WHITE
                + "\n &b " + MessageUtils.translateColorCodes("&bAqua")
                + ChatColor.WHITE
                + "\n &c " + MessageUtils.translateColorCodes("&cVermelho")
                + ChatColor.WHITE
                + "\n &d " + MessageUtils.translateColorCodes("&dRoxo Claro")
                + ChatColor.WHITE
                + "\n &e " + MessageUtils.translateColorCodes("&eAmarelo")
                + ChatColor.WHITE
                + "\n &f " + MessageUtils.translateColorCodes("&fBranco")
                + "\n "
        );
    }

}
