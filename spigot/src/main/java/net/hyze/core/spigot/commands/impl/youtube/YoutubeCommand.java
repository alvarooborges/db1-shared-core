package net.hyze.core.spigot.commands.impl.youtube;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.commands.impl.youtube.impl.AddVideoCommand;
import net.hyze.core.spigot.commands.impl.youtube.impl.AddYoutuberCommand;
import net.hyze.core.spigot.commands.impl.youtube.impl.InfoVideoCommand;
import net.hyze.core.spigot.commands.impl.youtube.impl.RemoveYoutuberCommand;

public class YoutubeCommand extends CustomCommand implements GroupCommandRestrictable {

    public YoutubeCommand() {
        super("youtuber", CommandRestriction.IN_GAME, "youtube", "yt");

        registerSubCommand(new AddVideoCommand());
        registerSubCommand(new InfoVideoCommand());
        registerSubCommand(new RemoveYoutuberCommand());
        registerSubCommand(new AddYoutuberCommand());
    }

    @Override
    public Group getGroup() {
        return Group.YOUTUBER;
    }

}
