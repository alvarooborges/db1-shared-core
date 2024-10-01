package net.hyze.core.shared.servers.storage.specs;

import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import net.hyze.core.shared.servers.Server;
import net.hyze.core.shared.servers.ServerType;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import java.sql.ResultSet;
import java.util.List;
import org.springframework.jdbc.core.ResultSetExtractor;

public abstract class SelectServersSpec extends SelectSqlSpec<List<Server>> {

    @Override
    public ResultSetExtractor<List<Server>> getResultSetExtractor() {
        return (ResultSet result) -> {
            List<Server> out = Lists.newArrayList();

            while (result.next()) {
                Optional<Server> serverOptional = Server.getById(result.getString("id"));

                if (serverOptional.isPresent()) {
                    String serverTypeRaw = result.getString("type");

                    Optional<ServerType> serverTypeOptional = Enums.getIfPresent(ServerType.class, serverTypeRaw);

                    if (serverTypeOptional.isPresent()) {
                        Server server = serverOptional.get();

                        server.setType(serverTypeOptional.get());
                        server.setDisplayName(result.getString("display_name"));

                        out.add(server);
                    }
                }
            }

            return out;
        };
    }
}
