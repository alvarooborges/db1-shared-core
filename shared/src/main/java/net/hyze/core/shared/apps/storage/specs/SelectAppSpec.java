package net.hyze.core.shared.apps.storage.specs;

import com.google.common.base.Enums;
import com.google.common.base.Optional;
import net.hyze.core.shared.apps.App;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.servers.Server;
import java.net.InetSocketAddress;
import java.sql.ResultSet;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import org.springframework.jdbc.core.ResultSetExtractor;

public abstract class SelectAppSpec extends SelectSqlSpec<App> {

    @Override
    public ResultSetExtractor<App> getResultSetExtractor() {
        return (ResultSet result) -> {
            while (result.next()) {
                String applicationType = result.getString("type");

                Optional<AppType> optional = Enums.getIfPresent(AppType.class, applicationType);

                if (optional.isPresent()) {
                    Server server = null;
                    String serverId = result.getString("server_id");

                    if (serverId != null) {
                        server = Server.getById(serverId).orNull();
                    }

                    App app = new App(
                            result.getString("id"),
                            result.getString("display_name"),
                            optional.get(),
                            new InetSocketAddress(result.getString("host"), result.getInt("port")),
                            server
                    );

                    if (server != null) {
                        server.addApp(app);
                    }

                    return app;
                }
            }

            return null;
        };
    }
}
