package net.hyze.core.spigot.misc.io.chunk;

import com.google.common.collect.Lists;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.List;

public abstract class ArrayDataFileStorage<T> {

    protected abstract void write(T data, ByteArrayDataOutput byteArray);

    protected abstract T read(ByteArrayDataInput byteArray);

    public void write(File file, List<T> data) throws IOException {
        ByteArrayDataOutput byteArray = ByteStreams.newDataOutput();

        byteArray.writeInt(data.size());

        data.forEach(d -> write(d, byteArray));

        FileOutputStream output = new FileOutputStream(file, false);
        FileChannel channel = output.getChannel();

        Channels.newOutputStream(channel).write(byteArray.toByteArray());

        output.close();
    }

    public List<T> read(File file) throws IOException {
        FileInputStream input = new FileInputStream(file);
        FileChannel channel = input.getChannel();

        InputStream stream = Channels.newInputStream(channel);

        byte[] targetArray = new byte[stream.available()];
        stream.read(targetArray);

        ByteArrayDataInput byteArray = ByteStreams.newDataInput(targetArray);

        int size = byteArray.readInt();

        List<T> out = Lists.newArrayList();

        for (int i = 0; i < size; i++) {
            T data = read(byteArray);

            if (data != null) {
                out.add(data);
            }
        }

        input.close();

        return out;
    }
}
