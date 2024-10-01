package net.hyze.core.spigot.misc.notification.actionbar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class ActionBarDefaultMessage {

    private final String message;
    private boolean preventNotification;
}
