package net.hyze.core.bungee.commands.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import net.hyze.core.bungee.commands.CustomCommand;
import net.hyze.core.bungee.messages.Message;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import okhttp3.HttpUrl;

public class DiscordCommand extends CustomCommand {

    public DiscordCommand() {
        super("discord", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        try {
            Algorithm algorithm = Algorithm.HMAC256("s9hBXdOJYIJU67ga");

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.MINUTE, 3);

            String token = JWT.create()
                    .withClaim("nick", user.getNick())
                    .withExpiresAt(calendar.getTime())
                    .withIssuer("hyze.net")
                    .sign(algorithm);

            URL url = new HttpUrl.Builder()
                    .scheme("https")
                    .host("discordapp.com")
                    .addPathSegments("api/oauth2/authorize")
                    .addQueryParameter("client_id", "573665542792282112")
                    .addQueryParameter("redirect_uri", "https://api.hyze.net/discord/callback")
                    .addQueryParameter("response_type", "code")
                    .addQueryParameter("scope", "identify")
                    .addQueryParameter("state", token)
                    .build().url();

            ComponentBuilder builder = new ComponentBuilder("Clique aqui para conectar sua conta do discord.")
                    .color(ChatColor.YELLOW)
                    .event(new ClickEvent(ClickEvent.Action.OPEN_URL, url.toString()));

            ProxiedPlayer player = (ProxiedPlayer) sender;

            player.sendMessage(builder.create());

            return;

        } catch (JWTCreationException | IllegalArgumentException ex) {
            ex.printStackTrace();
        }

        Message.ERROR.send(sender, "Algo de errado aconteceu!");
    }
}
