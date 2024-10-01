package net.hyze.core.shared.misc.utils;

import com.google.common.base.Joiner;
import java.util.Collection;
import java.util.regex.Pattern;
import lombok.Getter;

public enum Patterns {

    SPACE(" "),
    NEW_LINE("\n"),
    DOT("[.]"),
    SEMI_COLON(";"),
    COLON(":"),
    HYPHEN("-"),
    AT("@"),
    DOUBLE_SLASH("//"),
    COMERCIAL_E("&"),
    COLOR_CHARACTER("§"),
    PASSWORD("[a-zA-Z0-9_@#$%&*_\\-.]*"),
    NICK("[a-zA-Z0-9_]{3,16}"),
    EMAIL("^[-a-z0-9~!$%^&*_=+}{\\'?]+(\\.[-a-z0-9~!$%^&*_=+}{\\'?]+)*@([a-z0-9_][-a-z0-9_]*(\\.[-a-z0-9_]+)*\\.(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,5})?$"),
    EMAIL_MASK("(^[^@]{3}|(?!^)\\G)[^@]"),
    IP("^(?:(?:0?0?\\d|0?[1-9]\\d|1\\d\\d|2[0-5][0-5]|2[0-4]\\d)\\.){3}(?:0?0?\\d|0?[1-9]\\d|1\\d\\d|2[0-5][0-5]|2[0-4]\\d)$");

    @Getter
    private final Pattern pattern;

    @Getter
    private String regex;

    private Joiner joiner;

    private Patterns(String regex) {
        this.regex = regex;
        this.pattern = Pattern.compile(regex);
        this.joiner = Joiner.on(regex);
    }

    private Patterns(String regex, int flags) {
        this.pattern = Pattern.compile(regex, flags);
    }

    public String[] split(String input) {
        return pattern.split(input);
    }

    public boolean matches(String input) {
        return pattern.matcher(input).matches();
    }

    public String replace(String input, String replacement) {
        return pattern.matcher(input).replaceAll(replacement);
    }

    public String join(Collection<String> args) {
        return joiner.join(args);
    }

    public String join(String... args) {
        return joiner.join(args);
    }

    public static boolean matches(String regex, String input) {
        return Pattern.compile(regex).matcher(input).matches();
    }
}
