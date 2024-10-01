package net.hyze.core.shared.punishments;

import net.hyze.core.shared.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.TextComponent;

@Getter
@RequiredArgsConstructor
public abstract class PunishmentType {

    private final String name;

    public abstract TextComponent getMessage(User user, Punishment punishment);

    public abstract void apply(Punishment punishment);

}
