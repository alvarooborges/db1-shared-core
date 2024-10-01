package net.hyze.core.shared.punishments;

import com.google.common.collect.Lists;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.messages.MessageUtils;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;

@Getter
@RequiredArgsConstructor
public class PunishmentRevokeCategory {

    private final int id;

    private final String name;

    private final String displayName;

    private final Group group;

    private final String[] description;

    private final boolean enabled;

    public Collection<String> getFullDescription() {
        List<String> output = Lists.newArrayList();

        output.add(ChatColor.YELLOW + getDisplayName());
        output.add("");
        output.addAll(Lists.newArrayList(getDescription()));
        output.add("");
        output.add("Grupo mínimo: " + MessageUtils.translateColorCodes(group.getDisplayNameStriped()));

        return output;
    }
    
    public boolean isApprovedAppealRevokeCategory() {
        return this.name.equals(PunishmentConstants.APPROVED_APPEAL_REVOKE_CATEGORY);
    }

}
