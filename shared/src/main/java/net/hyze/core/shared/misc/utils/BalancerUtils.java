package net.hyze.core.shared.misc.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.apps.AppStatus;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.environment.Env;
import net.hyze.core.shared.servers.Server;
import net.hyze.core.shared.user.User;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.InputStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BalancerUtils {

    public static AppStatus fetchBestApp(User user, Server server, AppType type, User... friends) {
        HttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget;

        String baseUrl = String.format(
                "http://%s:%s",
                Env.getString("global.balancer.host", "h-1"),
                Env.getInt("global.balancer.port", 10404)
        );

        if (server != null) {
            httpget = new HttpGet(String.format(
                    "%s/best/%s/%s",
                    baseUrl,
                    server.name(),
                    type.name()
            ));
        } else {
            httpget = new HttpGet(String.format(
                    "%s/best/%s",
                    baseUrl,
                    type.name()
            ));
        }

        try {
            HttpResponse response = httpclient.execute(httpget);
            InputStream stream = response.getEntity().getContent();

            AppStatus status = CoreConstants.JACKSON.readValue(stream, AppStatus.class);

            if (status != null) {
                return status;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpget.reset();
        }

        return null;
    }
}
