package net.hyze.core.shared.echo.api;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class EchoBufferInput {

    private final ByteArrayDataInput buffer;

    public EchoBufferInput(byte[] bytes) {
        this.buffer = ByteStreams.newDataInput(bytes);
    }

    public boolean readBoolean() {
        return buffer.readBoolean();
    }

    public byte readByte() {
        return buffer.readByte();
    }

    public int readUnsignedByte() {
        return buffer.readUnsignedByte();
    }

    public short readShort() {
        return buffer.readShort();
    }

    public int readUnsignedShort() {
        return buffer.readUnsignedShort();
    }

    public char readChar() {
        return buffer.readChar();
    }

    public int readInt() {
        return buffer.readInt();
    }

    public long readLong() {
        return buffer.readLong();
    }

    public float readFloat() {
        return buffer.readFloat();
    }

    public double readDouble() {
        return buffer.readDouble();
    }

    public String readString() {
        boolean valid = buffer.readBoolean();

        if (!valid) {
            return null;
        }

        return buffer.readUTF();
    }

}
