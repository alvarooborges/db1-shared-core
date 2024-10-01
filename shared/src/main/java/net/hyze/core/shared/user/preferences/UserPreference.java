package net.hyze.core.shared.user.preferences;

import com.google.common.collect.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.servers.Server;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public class UserPreference {

    public static final String GENERIC_PREFERENCE_SERVER = "GENERIC";

    private final Table<Server, String, PreferenceStatus> serverPreferences;

    private final Map<String, PreferenceStatus> networkPreferences;

    public boolean hasPreference(Server server, String preferenceId) {
        return this.serverPreferences.contains(server, preferenceId);
    }

    public boolean hasPreference(String preferenceId) {
        Server server = CoreProvider.getApp().getServer();

        if (server == null) {
            return this.networkPreferences.containsKey(preferenceId);
        }

        return this.serverPreferences.contains(server, preferenceId);
    }

    public PreferenceStatus getPreference(Server server, String preferenceId) {
        if (this.hasPreference(server, preferenceId)) {
            return this.serverPreferences.get(server, preferenceId);
        }

        return PreferenceStatus.UNSET;
    }

    public PreferenceStatus getPreference(String id) {
        return getPreference(id, PreferenceStatus.UNSET);
    }

    public PreferenceStatus getPreference(String id, PreferenceStatus defaultStatus) {
        Server server = CoreProvider.getApp().getServer();

        if (server == null) {
            return hasPreference(id) ? this.networkPreferences.get(id) : defaultStatus;
        }

        return hasPreference(server, id) ? this.serverPreferences.get(CoreProvider.getApp().getServer(), id) : defaultStatus;
    }

    public void setPreference(Server server, String preferenceId, PreferenceStatus value) {
        this.serverPreferences.put(server, preferenceId, value);
    }

    public void setPreference(String preferenceId, PreferenceStatus value) {
        Server server = CoreProvider.getApp().getServer();

        if (server == null) {
            this.networkPreferences.put(preferenceId, value);
            return;
        }

        this.serverPreferences.put(server, preferenceId, value);
    }

    public Map<String, PreferenceStatus> getPreferences(Server server) {
        return serverPreferences.row(server);
    }

    public Map<String, PreferenceStatus> getPreferences() {
        return networkPreferences;
    }

}
