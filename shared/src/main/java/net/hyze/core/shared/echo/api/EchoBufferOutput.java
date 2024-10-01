package net.hyze.core.shared.echo.api;

import com.google.common.base.Charsets;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class EchoBufferOutput {

    private ByteArrayDataOutput buffer;

    public EchoBufferOutput() {
        this.buffer = ByteStreams.newDataOutput();
    }

    public void writeBoolean(boolean b) {
        buffer.writeBoolean(b);
    }

    public void writeByte(int i) {
        buffer.writeByte(i);
    }

    public void writeShort(int i) {
        buffer.writeShort(i);
    }

    public void writeChar(int i) {
        buffer.writeChar(i);
    }

    public void writeInt(int i) {
        buffer.writeInt(i);
    }

    public void writeLong(long l) {
        buffer.writeLong(l);
    }

    public void writeFloat(float v) {
        buffer.writeFloat(v);
    }

    public void writeDouble(double v) {
        buffer.writeDouble(v);
    }

    public void writeString(String s) {
        if (s != null) {
            buffer.writeBoolean(true);
            buffer.writeUTF(s);
        } else {
            buffer.writeBoolean(false);
        }
    }

    public byte[] toByteArray() {
        return buffer.toByteArray();
    }
}
