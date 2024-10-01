package net.hyze.core.spigot.commands.impl.youtube.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.Argument;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.misc.utils.HttpUtils;
import net.hyze.core.shared.misc.utils.NumberUtils;
import net.hyze.core.shared.misc.youtube.VideoInformation;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InfoVideoCommand extends CustomCommand implements GroupCommandRestrictable {

    public InfoVideoCommand() {
        super("info", CommandRestriction.IN_GAME);

        registerArgument(new Argument("id", "Id do vídeo."));
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        Player player = (Player) sender;

        String videoId = args[0].replace("&", "").replace("=", "");
        String channelId = CoreProvider.Repositories.YOUTUBERS.provide().isYoutuber(user.getId());

        if (!user.hasGroup(Group.GAME_MASTER)) {
            if (channelId == null) {
                Message.ERROR.send(sender, "Ops, seu canal não está registrado em nosso sistema. ");
                return;
            }

            if (CoreProvider.Repositories.YOUTUBERS.provide().containsYoutubeVideo(videoId)) {
                Message.ERROR.send(player, "Você já recebeu Cash por este vídeo.");
                return;
            }
        }

        String content;
        String url = "https://www.googleapis.com/youtube/v3/videos?id=" + videoId + "&part=snippet%2CcontentDetails%2Cstatistics&key=AIzaSyC70TCfMYaKmxh5SLoivLfw9_ns69tNNMk";
        JsonObject items;
        VideoInformation information;

        try {
            content = HttpUtils.getPage(url);
            items = CoreConstants.GSON.fromJson(content, JsonObject.class);
        } catch (IOException | JsonSyntaxException IOException) {
            Message.ERROR.send(player, "O vídeo informado não foi encontrado. :S");
            return;
        }

        try {
            information = CoreConstants.GSON.fromJson(items.getAsJsonArray("items").get(0), VideoInformation.class);
        } catch (Exception e) {
            Message.ERROR.send(player, "O vídeo informado não foi encontrado. :S");
            return;
        }

        if (!user.hasGroup(Group.GAME_MASTER) && !information.getSnippet().getChannelId().equalsIgnoreCase(channelId)) {
            Message.ERROR.send(player, "Este vídeo não pertence ao seu canal!");
            return;
        }

        try {

            String timeString = information.getSnippet()
                    .getPublishedAt()
                    .replace(".000Z", "")
                    .replace("T", " ");

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = format.parse(timeString);
            boolean canReceiveCash = date.getTime() > (System.currentTimeMillis() - (3L * (24L * (60L * 60000L))));

            String videoInfo = "\n &eCanal: &7%s"
                    + "\n &eTítulo do vídeo: &7%s"
                    + "\n &ePúblicado em: &7%s"
                    + "\n &eLikes: &a%s"
                    + "\n &eApto a receber Cash: &7%s"
                    + "\n ";

            String videoTitle = information.getSnippet().getTitle();

            Message.EMPTY.send(
                    player,
                    String.format(
                            videoInfo,
                            information.getSnippet().getChannelTitle(),
                            videoTitle.length() > 15 ? (videoTitle.substring(0, 15) + "...") : videoTitle,
                            format.format(date),
                            NumberUtils.format(information.getStatistics().getLikeCount()),
                            canReceiveCash ? "&aSim" : "&cNão"
                    )
            );

        } catch (ParseException ex) {
            Logger.getLogger(InfoVideoCommand.class.getName()).log(Level.SEVERE, null, ex);
            Message.ERROR.send(player, "Ops, algo de errado aconteceu!");
            return;
        }

    }

    @Override
    public Group getGroup() {
        return Group.YOUTUBER;
    }

}
