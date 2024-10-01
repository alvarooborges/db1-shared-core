package net.hyze.core.shared.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class Argument {

    private final String name;
    private final String description;
    private boolean required = true;

    public String getName() {
        return (required ? "<" : "[") + name + (required ? ">" : "]");
    }

    public boolean isValid(String arg) {
        return true;
    }

    public String getErroMessage(String arg) {
        return "";
    }
}
