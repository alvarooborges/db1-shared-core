package net.hyze.core.spigot.commands.impl.basics;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import java.util.Set;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SignCommand extends CustomCommand implements GroupCommandRestrictable {

    public SignCommand() {
        super("sign", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User requester, String[] args) {
        Player player = (Player) sender;
        Block targetBlock = player.getTargetBlock((Set) null, 10);

        if (targetBlock == null || !(targetBlock.getState() instanceof Sign)) {
            Message.ERROR.send(player, "Você não está olhando para uma placa!");
            return;
        }

        Sign sign = (Sign) targetBlock.getState();

        if (args.length < 2) {
            Message.ERROR.send(player, "Utilize /sign <line> <text>.");
            return;
        }

        int line;

        try {
            line = Integer.parseInt(args[0]);
        } catch (Exception e) {
            Message.ERROR.send(player, "Você especificou um número inválido: " + args[0] + ".");
            return;
        }

        if (line < 1 || line > 4) {
            Message.ERROR.send(player, "As linhas devem ser de 1 a 4!");
            return;
        }

        StringBuilder sb = new StringBuilder();

        for (int n = 1; n < args.length; n++) {
            sb.append(args[n]).append(" ");
        }

        String lineText = MessageUtils.translateColorCodes(sb.toString().trim());

        sign.setLine(line - 1, lineText);
        sign.update();

        Message.SUCCESS.send(player, "A linha " + line + " desta placa foi alterada com sucesso!");
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

}
