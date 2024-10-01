package net.hyze.core.shared.commands;

import net.hyze.core.shared.user.User;
import lombok.NonNull;

public interface CommandRestrictable {

    boolean canExecute(@NonNull User user);
    
    String getErrorMessage();
}
