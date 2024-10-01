package net.hyze.core.shared.misc.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Plural {

    SECOND("segundo", "segundos"),
    MINUTE("minuto", "minutos"),
    HOUR("hora", "horas"),
    DAY("dia", "dias"),
    COIN("coin", "coins"),
    LEVEL("level", "levels"),
    ITEM("item", "itens");

    @Getter
    private final String singular;

    @Getter
    private final String plural;

    public String of(long number) {
        return of(number, singular, plural);
    }

    public static String of(long number, String singular, String plural) {
        return number == 1 ? singular : plural;
    }

}
