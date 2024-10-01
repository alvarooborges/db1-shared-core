package net.hyze.core.shared.group;

import com.google.common.base.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.hyze.core.shared.messages.MessageUtils;
import net.md_5.bungee.api.ChatColor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum Group {
    GAME_MASTER,
    MANAGER,
    ADMINISTRATOR,
    MODERATOR,
    HELPER,
    YOUTUBER,
    MVP,
    VIP_PLUS,
    VIP,
    BUILDER,
    BETA,
    DEFAULT("Membro", new String(), ChatColor.GRAY, 0),
    ;

    @Setter
    private String displayNameRaw = new String();

    @Setter
    private String tagRaw = new String();

    @Setter
    private ChatColor color = ChatColor.GRAY;

    @Setter
    private int priority = 0;

    public boolean isHigher(Group group) {
        return group != null && this.getPriority() > group.getPriority();
    }

    public boolean isSameOrHigher(Group group) {
        return group != null && this.getPriority() >= group.getPriority();
    }

    public String getTagStriped() {
        return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', this.tagRaw));
    }

    public String getTag() {
        return MessageUtils.translateColorCodes(this.tagRaw);
    }

    public String getDisplayNameStriped() {
        return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', this.displayNameRaw));
    }

    public String getDisplayName() {
        return MessageUtils.translateColorCodes(this.displayNameRaw);
    }

    public String getDisplayTag() {
        return MessageUtils.translateColorCodes(String.format(
                "%s[%s]",
                this.color,
                this.tagRaw
        ));
    }

    public String getDisplayTag(String nick) {
        if (this.tagRaw.isEmpty()) {
            return this.color + nick;
        }

        return MessageUtils.translateColorCodes(this.getDisplayTag() + " " + nick);
    }

    public static Optional<Group> getById(String id) {

        if (id != null) {
            for (Group group : values()) {
                if (group.name().equalsIgnoreCase(id)) {
                    return Optional.of(group);
                }
            }
        }

        return Optional.absent();
    }
}
