package net.hyze.core.shared.servers;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.hyze.core.shared.apps.App;

@ToString(exclude = "apps")
@RequiredArgsConstructor
public enum Server {

    FACTIONS_ERAGON("factions-eragon", "factions_eragon", "era"),
    FACTIONS_EMPIRE("factions-empire", "factions_empire", "empire"),
    FACTIONS_HORUS("factions-horus", "factions_horus", "horus"),
    FACTIONS_MAGMA("factions-magma", "factions_magma", "magma"),
    FACTIONS_HYZE_ACADEMY("factions-academy", "factions-academy-spawn", "aca"),
    MINIGAMES("minigames", "minigames", "minigames"),
    SKYBLOCK("skyblock", "skyblock", "normal"),
    SKYBLOCK_CLASSIC("skyblock-classic", "skyblock_classic", "classic");

    @Getter
    private final String id;
    
    @Getter
    private final String envPath;

    @Getter
    private final String abbreviation;

    @Getter
    @Setter
    private ServerType type;

    @Getter
    @Setter
    private String displayName;

    private final Set<App> apps = Sets.newHashSet();

    public ImmutableList<App> getApps() {
        return ImmutableList.<App>builder().addAll(apps).build();
    }

    public void addApp(App app) {
        apps.add(app);
    }

    public static Optional<Server> getById(String id) {

        if (id != null) {
            for (Server server : values()) {
                if (server.getId().equals(id)) {
                    return Optional.of(server);
                }
            }
        }

        return Optional.absent();
    }
}
