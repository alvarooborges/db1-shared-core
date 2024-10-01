package net.hyze.core.shared.messages;

import net.hyze.core.shared.misc.utils.DefaultMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class MessageFormatter<T> {

    @Getter
    protected final String prefix;

    public String getMessage(String message) {
        return MessageUtils.translateColorCodes(this.prefix + message);
    }

    public abstract void send(T sender, String message);

    public void sendDefault(T sender, DefaultMessage defaultMessage, Object... objects) {
        this.send(sender, defaultMessage.format(objects));
    }
}
