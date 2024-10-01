package net.hyze.core.shared.party;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.echo.api.EchoBufferInput;
import net.hyze.core.shared.echo.api.EchoBufferOutput;
import net.hyze.core.shared.echo.packets.party.invite.TPartyInvitePacket;
import net.hyze.core.shared.environment.Env;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;

import java.util.Random;

public class PartyTest {

    public static void main2(String[] args) {
        System.out.println(Env.getString("cache.redis_main.host"));
        System.out.println(Env.getInt("cache.redis_main.port"));

        CoreProvider.Redis.REDIS_MAIN.prepare();

        Random random = new Random();
        int partyId = random.nextInt(100) + 1;
        System.out.println("aAAA...");

        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            jedis.subscribe(new BinaryJedisPubSub() {
                @Override
                public void onMessage(byte[] channel, byte[] message) {
                    EchoBufferInput buffer = new EchoBufferInput(message);
                    System.out.println(buffer.readBoolean());
                }
            },"teste".getBytes());

        }
        while(true){}
    }

    public static void main3(String[] args) {
        System.out.println(Env.getString("cache.redis_main.host"));
        System.out.println(Env.getInt("cache.redis_main.port"));

        CoreProvider.Redis.REDIS_MAIN.prepare();

        Random random = new Random();
        int partyId = random.nextInt(100) + 1;

        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            EchoBufferOutput buffer = new EchoBufferOutput();

            buffer.writeBoolean(false);

            byte[] ba = buffer.toByteArray();

            jedis.publish("teste".getBytes(), ba);
        }
    }

}
