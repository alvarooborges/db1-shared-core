package net.hyze.core.shared.misc.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
public class DefaultMessage {

    public static final DefaultMessage PLAYER_NOT_FOUND = new DefaultMessage("O jogador %s não foi encontrado.");
    public static final DefaultMessage PLAYER_NOT_ONLINE = new DefaultMessage("O jogador %s não está online.");
    public static final DefaultMessage NO_PERMISSION_STRICT = new DefaultMessage(
            "Você precisa do grupo %s para fazer isso.",
            "Você não tem permissão para fazer isso."
    );
    public static final DefaultMessage NO_PERMISSION = new DefaultMessage(
            "Você precisa do grupo %s ou superior para fazer isso.",
            "Você não tem permissão para fazer isso."
    );
    public static DefaultMessage COMBAT_TELEPORT_ERROR = new DefaultMessage("Você não pode se teletransportar enquanto estiver em combate.");
    public static DefaultMessage COMBAT_COMMAND_ERROR = new DefaultMessage("Você não pode executar este comando enquanto estiver em combate.");

    @Getter
    private final String rawMessage;

    @Getter
    @NonNull
    private String defaultRawMessage = "";

    public String format(Object... objects) {
        if (objects.length == 0 && !defaultRawMessage.isEmpty()) {
            return defaultRawMessage;
        }

        return String.format(this.rawMessage, objects);
    }
}
