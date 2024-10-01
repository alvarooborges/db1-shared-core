package net.hyze.core.spigot.misc.customitem;

public class IllegalRegistryException extends RuntimeException {

    public IllegalRegistryException() {
        super();
    }

    public IllegalRegistryException(String s) {
        super(s);
    }

    public IllegalRegistryException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalRegistryException(Throwable cause) {
        super(cause);
    }
}
