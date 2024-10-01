package net.hyze.core.shared.misc.utils;

import java.io.PrintStream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Printer {

    INFO(System.out),
    ERROR(System.err),
    DANGER(System.err);

    private final PrintStream stream;

    public void format(String format, Object... args) {
        stream.format(format, args);
    }

    public void coloredFormat(String format, Object... args) {

        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof String) {
                args[i] = parseColors((String) args[i]);
            }
        }

        stream.format(parseColors(format), args);
    }

    public void print(Object... lines) {
        for (Object line : lines) {
            stream.println(line);
        }
    }

    public void coloredPrint(final String... lines) {
        for (String line : lines) {
            this.stream.println(parseColors(line) + ShellColor.RESET.getShellColor());
        }
    }

    public String parseColors(String line) {
        for (ShellColor color : ShellColor.values()) {
            for (char c : color.getChars()) {
                line = line.replace("&" + c, color.getShellColor());
            }
        }

        return line;
    }

    public static enum ShellColor {

        BLACK("\u001B[30m", '0'),
        BLUE("\u001B[34m", '1', '9', 'b'),
        GREEN("\u001B[32m", 'a', '2'),
        CYAN("\u001B[36m", '3'),
        RED("\u001B[31m", 'c', '4', '6', 'd'),
        PURPLE("\u001B[35m", '5'),
        YELLOW("\u001B[33m", 'e'),
        WHITE("\u001B[37m", 'f', '7'),
        RESET("\u001B[0m", 'r'),
        BG_BLACK("\u001B[40m"),
        BG_BLUE("\u001B[44m"),
        BG_GREEN("\u001B[42m"),
        BG_CYAN("\u001B[46m"),
        BG_RED("\u001B[41m"),
        BG_PURPLE("\u001B[45m"),
        BG_YELLOW("\u001B[43m"),
        BG_WHITE("\u001B[47m");

        @Getter
        private final String shellColor;

        @Getter
        private final char[] chars;

        ShellColor(String shellColor, char... chars) {
            this.shellColor = shellColor;
            this.chars = chars;
        }

        @Override
        public String toString() {
            return this.shellColor;
        }

    }

}
