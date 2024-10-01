package net.hyze.core.shared.punishments;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PunishmentConstants {
    
    public static final String APPROVED_APPEAL_REVOKE_CATEGORY = "APPROVED_APPEAL";
    public static final Long MANAGER_ONLY_REVOKE_RESTRICTION_OFFSET = TimeUnit.DAYS.toMillis(12);
    public static final Long ADMIN_ONLY_REVOKE_RESTRICTION_OFFSET = TimeUnit.HOURS.toMillis(3);
    
    public static final Set<String> COMMANDS_AFFECTED_BY_MUTE = Sets.newHashSet("/g", "/tell", "/r", "/p", "/c");

}
