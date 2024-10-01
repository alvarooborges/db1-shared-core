package net.hyze.core.shared.echo.api;

public interface IByteSerializable {

    void write(EchoBufferOutput buffer);

    void read(EchoBufferInput buffer);

}
