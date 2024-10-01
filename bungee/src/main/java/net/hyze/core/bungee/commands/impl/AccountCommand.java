package net.hyze.core.bungee.commands.impl;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import net.hyze.core.bungee.commands.CustomCommand;
import net.hyze.core.bungee.messages.Message;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.commands.arguments.NickArgument;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.misc.utils.DateUtils;
import net.hyze.core.shared.misc.utils.Patterns;
import net.hyze.core.shared.user.User;
import net.hyze.core.shared.user.cache.redis.UserStatusRedisCache;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AccountCommand extends CustomCommand implements GroupCommandRestrictable {

    public AccountCommand() {
        super("account", "acc", "find");

        registerArgument(new NickArgument("nick", "", true));
    }

    @Override
    public Group getGroup() {
        return Group.MODERATOR;
    }

    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private final Function<Object, HoverEvent> CLICK_TO_COPY_HOVER = (obj) -> {

        ComponentBuilder componentBuilder = new ComponentBuilder("Clique para copiar: ")
                .color(ChatColor.GRAY)
                .append(obj.toString())
                .color(ChatColor.WHITE);

        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, componentBuilder.create());

    };

    private final Consumer<ComponentBuilder> JUMP_LINE = (componentBuilder) -> {
        componentBuilder
                .append("\n")
                .reset();
    };

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        User targetUser;

        if (args.length == 0) {

            if (isConsole(sender)) {
                Message.ERROR.send(sender, "Utilize \"/account <...>\".");
                return;
            }

            targetUser = user;

        } else {

            String input = args[0];
            String[] split = input.split(":");

            if (split.length == 2 && split[0].equalsIgnoreCase("id")) {

                Integer userId = Ints.tryParse(split[1]);

                if (userId == null) {
                    Message.ERROR.send(sender, "O ID informado é inválido. Utilize \"/account id:<ID>\".");
                    return;
                }

                targetUser = CoreProvider.Cache.Local.USERS.provide().get(userId);

                if (targetUser == null) {
                    Message.ERROR.send(sender, String.format("Nenhum usuário encontrado com o ID %d.", userId));
                    return;
                }

            } else if (user.hasGroup(Group.MANAGER) && Patterns.EMAIL.matches(input)) {

                targetUser = CoreProvider.Repositories.USERS.provide().fetchByEmail(input);

                if (targetUser == null) {
                    Message.ERROR.send(sender, String.format("Nenhum usuário encontrado com o e-mail \"%s\".", input));
                    return;
                }

            } else {

                targetUser = CoreProvider.Cache.Local.USERS.provide().get(input);

                if (targetUser == null) {
                    Message.ERROR.send(sender, String.format("Nenhum usuário encontrado com o nick \"%s\".", input));
                    return;
                }

            }

        }

        ChatColor primaryColor;
        ChatColor secondaryColor = ChatColor.WHITE;
        ChatColor tertiaryColor = ChatColor.GRAY;

        UserStatusRedisCache userStatusCache = CoreProvider.Cache.Redis.USERS_STATUS.provide();
        boolean isTargetUserOnline = userStatusCache.exists(targetUser.getNick());

        if (isTargetUserOnline) {
            if (targetUser.isLogged()) {
                primaryColor = ChatColor.GREEN;
            } else {
                primaryColor = ChatColor.YELLOW;
            }
        } else {
            primaryColor = ChatColor.RED;
        }

        ComponentBuilder componentBuilder = new ComponentBuilder("\n");

        /*
        Perfil
         */
        componentBuilder
                .append("Perfil")
                .color(primaryColor);

        JUMP_LINE.accept(componentBuilder);

        /*
        ID
         */
        componentBuilder
                .append("  ID: ")
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.valueOf(targetUser.getId())))
                .event(CLICK_TO_COPY_HOVER.apply(targetUser.getId()))
                .color(secondaryColor)
                .append(String.valueOf(targetUser.getId()))
                .color(tertiaryColor);

        JUMP_LINE.accept(componentBuilder);

        /*
        Nick
         */
        componentBuilder
                .append("  Nick: ")
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.valueOf(targetUser.getNick())))
                .event(CLICK_TO_COPY_HOVER.apply(targetUser.getNick()))
                .color(secondaryColor)
                .append(String.valueOf(targetUser.getNick()))
                .color(tertiaryColor);

        JUMP_LINE.accept(componentBuilder);

        /*
        UUID
         */
        componentBuilder.append("  UUID: ");

        String uuid;

        if (targetUser.hasUuid()) {

            uuid = targetUser.getUuid().toString();

            componentBuilder
                    .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, uuid))
                    .event(CLICK_TO_COPY_HOVER.apply(uuid));

        } else {
            uuid = "Não cadastrado.";
        }

        componentBuilder
                .color(secondaryColor)
                .append(uuid)
                .color(tertiaryColor);

        JUMP_LINE.accept(componentBuilder);

        if (user.hasGroup(Group.MANAGER)) {
        /*
        E-mail
         */
            componentBuilder.append("  E-mail: ");

            String email;

            if (targetUser.hasEmail()) {

                if (!targetUser.hasGroup(Group.ADMINISTRATOR)) {
                    email = targetUser.getEmail();
                } else {
                    email = targetUser.getEmail().replaceAll(Patterns.EMAIL_MASK.getRegex(), "$1*");
                }

                componentBuilder
                        .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, email))
                        .event(CLICK_TO_COPY_HOVER.apply(email));

            } else {
                email = "Não cadastrado.";
            }

            componentBuilder
                    .color(secondaryColor)
                    .append(email)
                    .color(tertiaryColor);

            JUMP_LINE.accept(componentBuilder);
        }

        /*
        Cadastrado em
         */
        componentBuilder
                .append("  Cadastrado em: ")
                .color(secondaryColor)
                .append(DATE_FORMAT.format(targetUser.getCreatedAt()))
                .color(tertiaryColor);

        JUMP_LINE.accept(componentBuilder);

        /*
        Contas associadas
         */
        List<User> associateUsers = CoreProvider.Repositories.USERS.provide().fetchAssociateUsers(targetUser, 3);

        List<BaseComponent[]> associateComponenets = associateUsers.stream()
                .map(User::getNick)
                .map(nick -> {
                    ComponentBuilder hover = new ComponentBuilder("Clique para ver: ")
                            .color(ChatColor.GRAY)
                            .append("/acc " + nick)
                            .color(ChatColor.WHITE);

                    return new ComponentBuilder(nick)
                            .color(secondaryColor)
                            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/acc " + nick))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover.create()))
                            .create();
                })
                .collect(Collectors.toList());

        componentBuilder
                .append("  Contas associadas: ")
                .color(secondaryColor);

        Iterator<BaseComponent[]> it = associateComponenets.iterator();
        while (it.hasNext()) {
            componentBuilder.append(it.next());

            if (it.hasNext()) {
                componentBuilder.append(", ", ComponentBuilder.FormatRetention.NONE);
            } else {
                componentBuilder.append(".", ComponentBuilder.FormatRetention.NONE);
            }

            componentBuilder.color(tertiaryColor);
        }

        JUMP_LINE.accept(componentBuilder);

        /*
        Status
         */
        if (isTargetUserOnline) {

            Response<String> targetUserProxyResponse;
            Response<String> targetUserServerResponse;
            Response<String> targetUserJoinedAtResponse;
            Response<String> targetUserIpResponse;
            Response<String> targetUserVersionResponse;

            Response<String> targetUserHyzeVersionResponse;
            Response<String> targetUserHyzeApiVersionResponse;
            Response<String> targetUserHardwareId;

            try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {

                Pipeline pipeline = jedis.pipelined();

                targetUserProxyResponse = userStatusCache.getProxy(targetUser.getNick(), pipeline);
                targetUserServerResponse = userStatusCache.getBukkitApp(targetUser.getNick(), pipeline);
                targetUserJoinedAtResponse = userStatusCache.getJoinedAt(targetUser.getNick(), pipeline);
                targetUserIpResponse = userStatusCache.getIp(targetUser.getNick(), pipeline);
                targetUserVersionResponse = userStatusCache.getVersion(targetUser.getNick(), pipeline);

                targetUserHyzeVersionResponse = userStatusCache.getHyzeClientVersion(targetUser.getNick(), pipeline);
                targetUserHyzeApiVersionResponse = userStatusCache.getHyzeClientApiVersion(targetUser.getNick(), pipeline);
                targetUserHardwareId = userStatusCache.getHardwareId(targetUser.getNick(), pipeline);

                pipeline.sync();

            }

            componentBuilder
                    .append("Status")
                    .color(primaryColor);

            JUMP_LINE.accept(componentBuilder);

            /*
            Proxy
             */
            componentBuilder
                    .append("  Proxy: ")
                    .color(secondaryColor)
                    .append(targetUserProxyResponse.get())
                    .color(tertiaryColor);

            JUMP_LINE.accept(componentBuilder);

            /*
            Server
             */
            if (user.hasGroup(Group.MANAGER)) {
                componentBuilder
                        .append("  Servidor: ")
                        .color(secondaryColor)
                        .append(targetUserServerResponse.get())
                        .color(tertiaryColor);

                JUMP_LINE.accept(componentBuilder);
            }

            /*
            Logado em
             */
            componentBuilder
                    .append("  Entrou em: ")
                    .color(secondaryColor)
                    .append(DATE_FORMAT.format(DateUtils.fromString(targetUserJoinedAtResponse.get())))
                    .color(tertiaryColor);

            JUMP_LINE.accept(componentBuilder);

            /*
            IP
             */
            if (user.hasGroup(Group.MANAGER)) {
                String ip = targetUserIpResponse.get();

                componentBuilder
                        .append("  IP: ")
                        .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ip))
                        .event(CLICK_TO_COPY_HOVER.apply(ip))
                        .color(secondaryColor)
                        .append(ip)
                        .color(tertiaryColor);
            }

            JUMP_LINE.accept(componentBuilder);

            /*
            IP
             */
            String version = targetUserVersionResponse.get();

            componentBuilder
                    .append("  Versão: ")
                    .color(secondaryColor)
                    .append(version)
                    .color(tertiaryColor);

            JUMP_LINE.accept(componentBuilder);
            Double hyzeVersion = Optional.ofNullable(targetUserHyzeVersionResponse.get()).map(Doubles::tryParse).orElse(null);
            Double hyzeApiVersion = Optional.ofNullable(targetUserHyzeApiVersionResponse.get()).map(Doubles::tryParse).orElse(null);
            String hardwareId = Optional.ofNullable(targetUserHardwareId.get()).orElse("Indefinido");

            if (hyzeVersion != null) {
                componentBuilder
                        .append("Cliente")
                        .color(primaryColor);

                JUMP_LINE.accept(componentBuilder);

                componentBuilder
                        .append("  Versão (Mod): ")
                        .color(secondaryColor)
                        .append(hyzeVersion.toString())
                        .color(tertiaryColor);

                JUMP_LINE.accept(componentBuilder);

                componentBuilder
                        .append("  Versão (API): ")
                        .color(secondaryColor)
                        .append(hyzeApiVersion == null ? "Indefinido" : hyzeApiVersion.toString())
                        .color(tertiaryColor);

                JUMP_LINE.accept(componentBuilder);

                componentBuilder
                        .append("  Hardware ID: ")
                        .color(secondaryColor)
                        .append(hardwareId)
                        .color(tertiaryColor);

                JUMP_LINE.accept(componentBuilder);
            }
        }

        sender.sendMessage(componentBuilder.create());

    }

}
