package net.hyze.core.shared.commands.arguments;

import net.hyze.core.shared.commands.Argument;
import net.hyze.core.shared.misc.utils.Patterns;

public class NickArgument extends Argument {

    public NickArgument(String name, String description, boolean required) {
        super(name, description, required);
    }

    public NickArgument(String name, String description) {
        super(name, description);
    }

    @Override
    public boolean isValid(String arg) {

        if (!Patterns.NICK.matches(arg)) {
            //Message.ERROR.send(sender, "O nick inserido é inválido.");
            return false;
        }

        return true;

    }

    @Override
    public String getErroMessage(String arg) {
        return "O nick inserido é inválido.";
    }
}
