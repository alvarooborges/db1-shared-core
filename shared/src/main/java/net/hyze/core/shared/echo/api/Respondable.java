package net.hyze.core.shared.echo.api;

public interface Respondable<T extends Response> {

    T getResponse();

    void setResponse(T response);
}
