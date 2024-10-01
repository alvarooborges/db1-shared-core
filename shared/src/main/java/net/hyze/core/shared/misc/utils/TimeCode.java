package net.hyze.core.shared.misc.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

@Getter
@RequiredArgsConstructor
public enum TimeCode {

    YEAR(12 * 30 * 24 * 60 * 60 * 1000L, "ano", "anos", new String[]{"year", "y"}),
    MONTH(30 * 24 * 60 * 60 * 1000L, "mês", "meses", new String[]{"month", "M"}),
    WEEK(7 * 24 * 60 * 60 * 1000L, "semana", "semanas", new String[]{"week", "w"}),
    DAY(24 * 60 * 60 * 1000L, "dia", "dias", new String[]{"day", "d"}),
    HOUR(60 * 60 * 1000L, "hora", "horas", new String[]{"hour", "h"}),
    MINUTE(60 * 1000L, "minuto", "minutos", new String[]{"min", "m"}),
    SECOND(1000L, "segundo", "segundos", new String[]{"sec", "seg", "s"});

    private final long millis;
    private final String single;
    private final String plural;
    private final String[] aliases;

    public static TimeCode getUnit(String alias) {
        for (TimeCode unit : TimeCode.values()) {
            for (String s : unit.getAliases()) {
                if (s.equals(alias)) {
                    return unit;
                }
            }

            if (unit.getSingle().equals(alias) || unit.getPlural().equals(alias)) {
                return unit;
            }
        }

        return null;
    }

    public static long parse(String text) {

        if (text.equalsIgnoreCase("0")) {
            return 0L;
        }

        long time = 0L;
        StringBuilder charBuffer = new StringBuilder();
        String valueBuffer = "";
        boolean next = false;

        for (char c : text.toCharArray()) {
            boolean number = Character.isDigit(c);
            boolean letter = Character.isAlphabetic(c);

            if (number) {
                if (charBuffer.length() > 0) {
                    int intBuffer = valueBuffer.isEmpty() ? -5 : Integer.parseInt(valueBuffer);

                    if (intBuffer > 0) {
                        String alias = charBuffer.toString();
                        TimeCode unit = TimeCode.getUnit(alias);

                        if (unit != null) {
                            time += unit.getMillis() * intBuffer;
                        }
                    }

                    charBuffer.delete(0, charBuffer.length());
                }

                if (next) {
                    valueBuffer = "";
                    next = false;
                }

                valueBuffer += c;
            } else if (letter) {
                charBuffer.append(c);
                next = true;
            }
        }

        if (charBuffer.length() > 0) {
            int intBuffer = valueBuffer.isEmpty() ? -5 : Integer.parseInt(valueBuffer);

            if (intBuffer > 0) {
                String alias = charBuffer.toString();
                TimeCode unit = TimeCode.getUnit(alias);

                if (unit != null) {
                    time += unit.getMillis() * intBuffer;
                }
            }
        }

        return time;
    }

    public static String toText(long time, int length) {
        StringBuilder builder = new StringBuilder();

        int aux = 0;

        for (TimeCode unit : TimeCode.values()) {

            if (aux == length) {
                break;
            }

            int amount = Math.round(time / unit.getMillis());

            if (amount > 0) {
                String t = amount == 1 ? unit.getSingle() : unit.getPlural();
                String text = builder.length() <= 0 ? amount + " " + t : ", " + amount + " " + t;

                builder.append(text);
                aux++;
            }

            time -= amount * unit.getMillis();
        }

        for (int i = builder.length(); i > 0; i--) {
            int a = i - 1;
            char c = builder.charAt(a);

            if (c == ',') {
                builder.replace(a, a + 1, " e");
                break;
            }
        }

        return builder.toString();
    }

    public static String getFormattedTimeLeft(long millis) {
        if (millis < 0) {
            return "";
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);

        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);

        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);

        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);

        if (days > 0) {
            sb.append(days).append("d ");
        }

        if (hours > 0) {
            sb.append(hours).append("h ");
        }

        if (minutes > 0) {
            sb.append(minutes).append("m ");
        }

        if (days == 0 && hours == 0 && minutes == 0 && seconds == 1 && millis < 1000) {

            double halfsec = millis / 1000D;

            NumberFormat nf = new DecimalFormat("#.##");
            String value = nf.format(NumberUtils.roundDouble(halfsec, 2));

            sb.append(value).append("s");

        } else if (seconds > 0) {

            sb.append(seconds).append("s");

        }

        return (sb.toString().trim());
    }
}