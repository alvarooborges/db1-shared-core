package net.hyze.core.shared.echo.api;

import net.hyze.core.shared.CoreProvider;
import java.lang.reflect.InvocationTargetException;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.hyze.core.shared.misc.utils.Printer;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.BinaryJedisPubSub;

@RequiredArgsConstructor
public class EchoSubscriber extends BinaryJedisPubSub {

    public static boolean DEBUG = false;

    private final BiConsumer<EchoPacketHeader, EchoPacket> executor;

    private final Echo echo;

    @Override
    public void onMessage(byte[] channel, byte[] message) {
        try {
            EchoBufferInput buffer = new EchoBufferInput(message);

            Class<? extends EchoPacket> clazz;

            String className = buffer.readString();

            try {
                clazz = (Class<? extends EchoPacket>) Class.forName(className);
            } catch (ClassNotFoundException ex) {

                if (DEBUG) {
                    Printer.ERROR.print("Este projeto não suporta o pacote " + className);
                    Printer.ERROR.print(Arrays.toString(message));
                    Printer.ERROR.print("==============================================");
                }

                return;
            } catch (ClassCastException ex) {
                if (DEBUG) {
                    Printer.ERROR.print("Pacote " + className + " inválido.");
                }

                ex.printStackTrace();
                return;
            }

            EchoPacket packet;

            try {
                EchoPacketHeader header = new EchoPacketHeader();
                header.read(buffer);

                boolean debug = clazz.getAnnotation(DebugPacket.class) != null;

                if (!header.getSender().equals(CoreProvider.getApp())) {

                    if (!echo.isListening(clazz, header)) {
                        if (debug) {
                            Printer.INFO.print("Nenhum listener - " + clazz.getSimpleName());
                        }
                        return;
                    }

                    packet = clazz.getDeclaredConstructor().newInstance();
                    packet.read(buffer);

                    packet.setHandleHeader(header);

                    if (debug) {
                        Printer.INFO.print("Aceitando - " + clazz.getSimpleName());
                    }
                    
                    executor.accept(header, packet);
                } else {
                    if (debug) {
                        Printer.INFO.print("Mesmo app - " + clazz.getSimpleName());
                    }
                }
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
