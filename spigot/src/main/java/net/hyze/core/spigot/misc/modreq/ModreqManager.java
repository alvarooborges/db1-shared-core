package net.hyze.core.spigot.misc.modreq;

import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ModreqManager {

    public static final Map<User, Long> REQUESTS = Maps.newHashMap();

    @NonNull
    public static Group STAFF_GROUP = Group.MODERATOR;

    public static final long DELAY = TimeUnit.MINUTES.toMillis(30);
}
