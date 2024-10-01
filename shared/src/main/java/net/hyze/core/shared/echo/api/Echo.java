package net.hyze.core.shared.echo.api;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.App;
import net.hyze.core.shared.misc.utils.Printer;
import net.hyze.core.shared.providers.RedisProvider;

import java.util.*;
import java.util.function.Consumer;

import net.hyze.core.shared.servers.Server;
import lombok.Getter;
import lombok.NonNull;
import redis.clients.jedis.Jedis;

import java.util.function.BiConsumer;

import lombok.RequiredArgsConstructor;
import org.greenrobot.eventbus.EventBus;
import redis.clients.jedis.util.SafeEncoder;

@RequiredArgsConstructor
public class Echo {

    public static final String CHANNEL_BASE_NAME = "dev.minecraft.echo";

    private final RedisProvider redisProvider;

    private final EventBus eventBus = EventBus.builder()
            .logNoSubscriberMessages(false)
            .throwSubscriberException(true)
            .build();

    @Getter
    private final HashMap<UUID, Consumer<? extends Response>> consumers = Maps.newHashMap();

    @Getter
    private BiConsumer<EchoPacketHeader, EchoPacket> executor;

    public <R extends Response, T extends EchoPacket & Respondable<R>> void publish(T packet, Consumer<R> onResponse) {
        publish(packet, null, onResponse);
    }

    public <R extends Response, T extends EchoPacket & Respondable<R>> void publish(T packet, App target, Consumer<R> onResponse) {
        UUID responseUUID = UUID.randomUUID();
        consumers.put(responseUUID, (Consumer<R>) onResponse);

        UUID uuid = Optional.ofNullable(packet.getHandleHeader())
            .map(EchoPacketHeader::getUUID)
            .orElse(UUID.randomUUID());

        EchoPacketHeader header = new EchoPacketHeader(
            CoreProvider.getApp(),
            target,
            CoreProvider.getApp().getServer(),
            uuid,
            responseUUID,
            null
        );

        publish(packet, header);
    }

    public <T extends EchoPacket> void publish(T packet) {
        publish(packet, CoreProvider.getApp().getServer());
    }

    public <T extends EchoPacket> void publish(T packet, Server server) {
        UUID uuid = Optional.ofNullable(packet.getHandleHeader())
                .map(EchoPacketHeader::getUUID)
                .orElse(UUID.randomUUID());

        publish(packet, new EchoPacketHeader(CoreProvider.getApp(), server, uuid));
    }

    private <T extends EchoPacket> void publish(T packet, EchoPacketHeader header) {
        String channel = CHANNEL_BASE_NAME;

        Class clazz = packet.getClass();
        boolean debug = clazz.getAnnotation(DebugPacket.class) != null;

        if (packet.getClass().isAnnotationPresent(ServerPacket.class)) {
            if (header.getSenderServer() != null) {
                channel += "." + header.getSenderServer().getId();
            } else {
                if (debug) {
                    Printer.INFO.print("Server Packet error - " + clazz.getSimpleName());
                }
                return;
            }
        }

        if (!(packet instanceof Respondable) || ((Respondable) packet).getResponse() == null) {
            if (packet.getClass().isAnnotationPresent(ExternalPacket.class)) {
                ExternalPacket externalPacket = packet.getClass().getAnnotation(ExternalPacket.class);
                channel += "." + externalPacket.channel();
            }
        }

        if (debug) {
            Printer.INFO.print(String.format("Channel - %s - %s", clazz.getSimpleName(), channel));
        }

        header.setChannel(channel);

        EchoBufferOutput buffer = new EchoBufferOutput();

        buffer.writeString(packet.getClass().getName());

        header.write(buffer);
        packet.write(buffer);

        if (executor != null) {
            if (debug) {
                Printer.INFO.print(String.format("Local executor - %s", clazz.getSimpleName()));
            }

            packet.setHandleHeader(header);
            executor.accept(header, packet);
        }

        try (Jedis jedis = redisProvider.provide().getResource()) {
            if (debug) {
                Printer.INFO.print(String.format("Publish - %s", clazz.getSimpleName()));
            }

            jedis.publish(channel.getBytes(), buffer.toByteArray());
        }
    }

    public void subscribe(@NonNull BiConsumer<EchoPacket, Runnable> consumer) {
        List<String> channels = Lists.newArrayList(
                CHANNEL_BASE_NAME
        );

        if (CoreProvider.getApp() != null && CoreProvider.getApp().getServer() != null) {
            channels.add(CHANNEL_BASE_NAME + "." + CoreProvider.getApp().getServer().getId());
        }

        subscribe(consumer, channels);
    }

    public void subscribe(@NonNull BiConsumer<EchoPacket, Runnable> consumer, Collection<String> channels) {
        executor = (header, packet) -> {

            Class clazz = packet.getClass();
            boolean debug = clazz.getAnnotation(DebugPacket.class) != null;

            /*
              Se o pacote for a resposta de outro pacote.
             */
            if (packet instanceof Response) {

                if (debug) {
                    Printer.INFO.print(String.format("Response Packet - %s", clazz.getSimpleName()));
                }

                UUID responseUUID = header.getResponseUUID();
                Consumer responseConsumer = consumers.remove(responseUUID);

                if (responseConsumer != null) {
                    if (debug) {
                        Printer.INFO.print(String.format("Accept Response Consumer - %s", clazz.getSimpleName()));
                    }

                    responseConsumer.accept(packet);
                } else {
                    if (debug) {
                        Printer.INFO.print(String.format("Response Consumer is null - %s", clazz.getSimpleName()));
                    }
                }

                return;
            }

            consumer.accept(packet, () -> {
                /*
                  Se o pacote for Respondable ele deve ser ouvido somente pelo
                  app alvo.
                 */
                if (packet instanceof Respondable) {
                    if (!CoreProvider.getApp().equals(header.getTarget())) {
                        return;
                    }
                }

                eventBus.post(packet);

                if (packet instanceof Respondable) {

                    UUID responseUUID = header.getResponseUUID();

                    EchoPacketHeader newHeader = new EchoPacketHeader(CoreProvider.getApp(), header.getSender(), header.getSenderServer(), UUID.randomUUID(), responseUUID, header.getChannel());

                    try {
                        Respondable respondable = (Respondable) packet;
                        Response response = respondable.getResponse();

                        if (response != null) {
                            publish(response, newHeader);
                        } else {
                            Class responseType = respondable.getClass().getMethod("getResponse").getReturnType();
                            publish((Response) responseType.newInstance(), newHeader);
                        }
                    } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        };

        new Thread(() -> {
            try (Jedis jedis = redisProvider.provide().getResource()) {
                jedis.subscribe(new EchoSubscriber(executor, Echo.this), SafeEncoder.encodeMany(channels.stream().toArray(String[]::new)));
            }
        }, "Thread-Echo-Subscriber").start();
    }

    public void registerListener(EchoListener listener) {
        eventBus.register(listener);
    }

    public boolean isListening(Class<? extends EchoPacket> clazz, EchoPacketHeader header) {
        if (eventBus.hasSubscriberForEvent(clazz)) {
            return true;
        }

        if (Response.class.isAssignableFrom(clazz)) {
            return header.getResponseUUID() == null ? false : consumers.containsKey(header.getResponseUUID());
        }

        return false;
    }
}
