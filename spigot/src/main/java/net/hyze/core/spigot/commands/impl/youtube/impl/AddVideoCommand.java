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
import net.hyze.core.shared.misc.youtube.VideoInformation;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.Title;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddVideoCommand extends CustomCommand implements GroupCommandRestrictable {

    public AddVideoCommand() {
        super("video", CommandRestriction.IN_GAME);

        registerArgument(new Argument("id", "Id do vídeo."));
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        Player player = (Player) sender;

        String channelId = CoreProvider.Repositories.YOUTUBERS.provide().isYoutuber(user.getId());

        String videoId = args[0].replace("&", "").replace("=", "");

        if (channelId == null) {
            Message.ERROR.send(sender, "Ops, seu canal não está registrado em nosso sistema. ");
            return;
        }

        if (CoreProvider.Repositories.YOUTUBERS.provide().containsYoutubeVideo(videoId)) {
            Message.ERROR.send(player, "Você já recebeu Cash por este vídeo.");
            return;
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

        if (!information.getSnippet().getChannelId().equalsIgnoreCase(channelId)) {
            Message.ERROR.send(player, "Tá tentando burlar o sistema? @_@");
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

            if (!canReceiveCash) {
                Message.ERROR.send(player, "Ops, seu vídeo foi postado a mais de três dias. :(");
                return;
            }

        } catch (ParseException ex) {
            Logger.getLogger(AddVideoCommand.class.getName()).log(Level.SEVERE, null, ex);
            Message.ERROR.send(player, "Ops, algo de errado aconteceu!");
            return;
        }

        int amount = information.getStatistics().getLikeCount();

        if (amount > 2000) {
            amount = 2000;
        }

        new Title().subTitle("&aCash").title("&a+" + amount).fadeIn(20).fadeOut(20).stay(80).send(player);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);

        Message.SUCCESS.send(sender, "+" + amount + " Cash adicionados em sua conta!");

        CoreProvider.Repositories.YOUTUBERS.provide().insertYoutubeVideo(user.getId(), channelId, videoId, information);

        user.incrementCash(amount);

    }

    @Override
    public Group getGroup() {
        return Group.YOUTUBER;
    }

}
