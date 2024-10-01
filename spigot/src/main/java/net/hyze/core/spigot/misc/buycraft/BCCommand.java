package net.hyze.core.spigot.misc.buycraft;

import com.google.common.base.Enums;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.Argument;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.echo.packets.SendMessagePacket;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.misc.purchases.Purchase;
import net.hyze.core.shared.misc.purchases.PurchaseState;
import net.hyze.core.shared.misc.purchases.PurchaseType;
import net.hyze.core.shared.misc.purchases.storage.specs.InsertPurchaseSpec;
import net.hyze.core.shared.misc.utils.Printer;
import net.hyze.core.shared.servers.Server;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

public class BCCommand extends CustomCommand {

    public BCCommand() {
        super("bc", CommandRestriction.CONSOLE_AND_IN_GAME);

        registerArgument(new Argument("action", "ação [addvip|addcash]"));
        registerArgument(new Argument("nick", "nick do jogador"));
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        if (sender instanceof Player) {

            if (!user.hasGroup(Group.GAME_MASTER)) {
                Message.ERROR.send(sender, "Sem permissão.");
                return;
            }
        }

        String action = args[0].toLowerCase();

        String nick = args[1];

        // bc addcash {name} 100 {transaction} {purchaseQuantity} {currency} {price} {packagePrice} {ip} {email}
        if ("addcash".equals(action)) {
            if (args.length < 8) {
                Printer.ERROR.print("Algo de errado aconteceu ao tentar adicionar cash [invaid arguments]", Arrays.toString(args));
                Message.ERROR.send(sender, "Algo de errado aconteceu ao tentar adicionar cash [invaid arguments]");
                return;
            }

            String amountRaw = args[2];
            String transaction = args[3];
            String quantityRaw = args[4];
            String currency = args[5];
            String paidPriceRaw = args[6];
            String originalPriceRaw = args[7];
            String ip = "127.0.0.1";
            String email = "admin@admin.com";

            if (args.length >= 10) {
                ip = args[8];
                email = args[9];
            }

            User target = CoreProvider.Repositories.USERS.provide().fetchByNick(nick);

            if (target == null) {
                Printer.ERROR.print("Algo de errado aconteceu ao tentar adicionar cash [invaid user]", Arrays.toString(args));
                Message.ERROR.send(sender, "Algo de errado aconteceu ao tentar adicionar cash [invaid user]");
                return;
            }

            Purchase purchase = new Purchase(
                    0,
                    target.getId(),
                    null,
                    PurchaseType.CASH,
                    amountRaw,
                    0,
                    transaction,
                    Ints.tryParse(quantityRaw),
                    currency,
                    Doubles.tryParse(paidPriceRaw),
                    Doubles.tryParse(originalPriceRaw),
                    ip,
                    email,
                    PurchaseState.PENDING,
                    PurchaseState.PENDING,
                    new Date()
            );

            InsertPurchaseSpec.Response response = CoreProvider.Repositories.PURCHASES.provide().insert(purchase);

            if (response != InsertPurchaseSpec.Response.SUCCESS) {
                Printer.ERROR.print(response, "Algo de errado aconteceu ao tentar adicionar cash [invaid sucess]", Arrays.toString(args));
                Message.ERROR.send(sender, "Algo de errado aconteceu ao tentar adicionar cash [invaid sucess]");
                return;
            }

            Printer.ERROR.print(response, "Cash adicionado com sucesso", Arrays.toString(args));
            Message.SUCCESS.send(sender, "Cash adicionado com sucesso. Aguarde para ativação automática.");

            ComponentBuilder builder = new ComponentBuilder("\nAcabamos de ativar sua compra de " + amountRaw + " cash.")
                    .color(ChatColor.GREEN)
                    .append("\nRelogue para ativar!\n");

            SendMessagePacket messagePacket = new SendMessagePacket(Collections.singleton(target), builder.create());
            CoreProvider.Redis.ECHO.provide().publish(messagePacket);

            return;
        }

        // bc addvip {name} factions-eragon VIP {packageExpiry} {transaction} {purchaseQuantity} {currency} {price} {packagePrice} {ip} {email}
        if ("addvip".equals(action)) {

            if (args.length < 10) {
                Printer.ERROR.print("Algo de errado aconteceu ao tentar adicionar um vip [invaid arguments]", Arrays.toString(args));
                Message.ERROR.send(sender, "Algo de errado aconteceu ao tentar adicionar um vip [invaid arguments]");
                return;
            }

            String serverId = args[2];
            String groupId = args[3];
            String expiryRaw = args[4];
            String transaction = args[5];
            String quantityRaw = args[6];
            String currency = args[7];
            String paidPriceRaw = args[8];
            String originalPriceRaw = args[9];
            String ip = "127.0.0.1";
            String email = "admin@admin.com";

            if (args.length >= 12) {
                ip = args[10];
                email = args[11];
            }

            User target = CoreProvider.Repositories.USERS.provide().fetchByNick(nick);

            if (target == null) {
                Printer.ERROR.print("Algo de errado aconteceu ao tentar adicionar um vip [invaid user]", Arrays.toString(args));
                Message.ERROR.send(sender, "Algo de errado aconteceu ao tentar adicionar um vip [invaid user]");
                return;
            }

            Server server = Server.getById(serverId).orNull();

            if (server == null) {
                Printer.ERROR.print("Algo de errado aconteceu ao tentar adicionar um vip [invaid server]", Arrays.toString(args));
                Message.ERROR.send(sender, "Algo de errado aconteceu ao tentar adicionar um vip [invaid server]");
                return;
            }

            Group group = Enums.getIfPresent(Group.class, groupId).orNull();

            if (group == null) {
                Printer.ERROR.print("Algo de errado aconteceu ao tentar adicionar um vip [invaid group]", Arrays.toString(args));
                Message.ERROR.send(sender, "Algo de errado aconteceu ao tentar adicionar um vip [invaid group]");
                return;
            }

            Purchase purchase = new Purchase(
                    0,
                    target.getId(),
                    server,
                    PurchaseType.VIP,
                    group.name(),
                    Ints.tryParse(expiryRaw),
                    transaction,
                    Ints.tryParse(quantityRaw),
                    currency,
                    Doubles.tryParse(paidPriceRaw),
                    Doubles.tryParse(originalPriceRaw),
                    ip,
                    email,
                    PurchaseState.PENDING,
                    PurchaseState.PENDING,
                    new Date()
            );

            InsertPurchaseSpec.Response response = CoreProvider.Repositories.PURCHASES.provide().insert(purchase);

            if (response != InsertPurchaseSpec.Response.SUCCESS) {
                Printer.ERROR.print(response, "Algo de errado aconteceu ao tentar adicionar um vip [invaid sucess]", Arrays.toString(args));
                Message.ERROR.send(sender, "Algo de errado aconteceu ao tentar adicionar um vip [invaid sucess]");
                return;
            }

            Printer.INFO.print(response, "Vip adicionado com sucesso", Arrays.toString(args));
            Message.SUCCESS.send(sender, "Vip adicionado com sucesso. Aguarde para ativação automática.");

//            if (server == Server.FACTIONS_EMPIRE) {
//                int expire = Ints.tryParse(expiryRaw);
//
//                boolean permanent = expire < 0;
//                int bonus = 0;
//
//                switch (group) {
//                    case VIP:
//                        bonus = permanent ? 1000 : expire == 90 ? 750 : 500;
//                        break;
//                    case VIP_PLUS:
//                        bonus = permanent ? 2000 : expire == 90 ? 1750 : 1500;
//                        break;
//                    case MVP:
//                        bonus = permanent ? 3000 : expire == 90 ? 2750 : 2500;
//                        break;
//                }
//
//                if (bonus > 0) {
//                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), String.format(
//                            "bc addcash %s %s %s 1 %s 0 0 %s %s",
//                            target.getNick(),
//                            bonus,
//                            transaction,
//                            currency,
//                            ip,
//                            email
//                    ));
//                }
//            }

            ComponentBuilder builder = new ComponentBuilder("\nAcabamos de ativar sua compra de " + group.getDisplayNameStriped() + ".")
                    .color(ChatColor.GREEN)
                    .append("\nRelogue para ativar!\n");

            SendMessagePacket messagePacket = new SendMessagePacket(Collections.singleton(target), builder.create());
            CoreProvider.Redis.ECHO.provide().publish(messagePacket);
            return;
        }
    }
}
