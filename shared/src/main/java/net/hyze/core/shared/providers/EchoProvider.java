package net.hyze.core.shared.providers;

import net.hyze.core.shared.contracts.Provider;
import net.hyze.core.shared.echo.api.Echo;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EchoProvider implements Provider<Echo> {

    private final Supplier<RedisProvider> redisCacheProvide;

    private Echo echo;

    @Override
    public void prepare() {
        echo = new Echo(redisCacheProvide.get());
    }

    @Override
    public Echo provide() {
        return echo;
    }

    @Override
    public void shut() {
    }
}
