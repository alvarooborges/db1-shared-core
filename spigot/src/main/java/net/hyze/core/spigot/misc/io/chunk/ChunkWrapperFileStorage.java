package net.hyze.core.spigot.misc.io.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public abstract class ChunkWrapperFileStorage<T> extends ArrayDataFileStorage<ChunkWrapperFileStorage.Item<T>> {

    protected final int x, z;
    protected final String worldName;
    protected final File directory;

    @Getter
    private final Map<Vector, T> map = Maps.newConcurrentMap();

    protected abstract void writeData(T data, ByteArrayDataOutput byteArray);

    protected abstract T readData(ByteArrayDataInput byteArray);

    @Override
    protected void write(ChunkWrapperFileStorage.Item<T> item, ByteArrayDataOutput byteArray) {
        byteArray.writeDouble(item.vector.getX());
        byteArray.writeDouble(item.vector.getY());
        byteArray.writeDouble(item.vector.getZ());

        writeData(item.data, byteArray);
    }

    @Override
    protected ChunkWrapperFileStorage.Item<T> read(ByteArrayDataInput byteArray) {
        double x = byteArray.readDouble();
        double y = byteArray.readDouble();
        double z = byteArray.readDouble();

        T data = readData(byteArray);

        if (data != null) {
            return new Item<>(new Vector(x, y, z), data);
        }

        return null;
    }

    public void save() {
        File file = new File(directory, String.format("r.%s.%s.%s.bp", this.x, this.z, this.worldName));

        if (this.map.isEmpty()) {
            if (file.exists()) {
                file.delete();
            }
        } else {
            try {
                List<ChunkWrapperFileStorage.Item<T>> list = Lists.newArrayList();

                this.map.forEach((Vector key, T value) -> list.add(new Item<>(key, value)));

                this.write(file, list);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void load() {
        File file = new File(directory, String.format("r.%s.%s.%s.bp", this.x, this.z, this.worldName));

        if (!file.exists()) {
            return;
        }

        try {
            List<ChunkWrapperFileStorage.Item<T>> data = this.read(file);

            data.forEach(d -> this.map.put(d.vector, d.data));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @AllArgsConstructor
    public static class Item<U> {
        private Vector vector;
        private U data;
    }
}
