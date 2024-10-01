package net.hyze.core.shared.misc.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.TreeMap;

public class NumberUtils {

    private static final DecimalFormat DECIMAL_FORMATTER = new DecimalFormat("#.##");
    private static final NumberFormat format = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
    private static final NumberFormat nf = new DecimalFormat("#,##0.##");

    @Deprecated
    public static Integer asInteger(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    @Deprecated
    public static Double asDouble(String input) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static Double proportionalValue(double firstX, double lastX, double firstY, double lastY, double pointX) {
        return (firstY - lastY) / (lastX - firstX) * (lastX - pointX) + lastY;
    }

    public static String formatDecimal(double number) {
        return DECIMAL_FORMATTER.format(number);
    }

    public static String format(int number) {
        return format.format(number);
    }

    public static String format(double number) {
        return format.format(roundDouble(number, 2));
    }

    public static String format(long number) {
        return format.format(number);
    }

    public static double roundDouble(double base, int decimalCases) {
        return new BigDecimal(base).setScale(decimalCases, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static float roundFloat(float base, int decimalCases) {
        return new BigDecimal(base).setScale(decimalCases, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public static double getKdr(int kills, int deaths) {
        return deaths == 0 ? kills : (kills == 0 || deaths > kills ? 0 : roundDouble((double) kills / deaths, 1));
    }

    public static int getDoubleLength(double number) {
        return String.valueOf(number).length();
    }

    public static int getIntegerLength(int number) {
        return String.valueOf(number).length();
    }

    public static String toRoman(int number) {
        int l = ROMAN_MAP.floorKey(number);
        return number == l ? ROMAN_MAP.get(number) : ROMAN_MAP.get(l) + toRoman(number - l);
    }

    public static String toK(double count) {
        if (count < 1000D) {
            return String.format("%.2f", count);
        }

        int exp = (int) (Math.log(count) / Math.log(1000D));

        return String.format(
                "%.1f%c",
                count / Math.pow(1000D, exp),
                "kMBTPE".charAt(exp - 1)
        );
    }

    /**
     * @param value
     * @return Numero formatado como '#.##0,##'
     */
    public static String formatDisplayNumber(double value) {
        return nf.format(value).replace('.', ';').replace(',', '.').replace(';', ',');
    }

    /**
     * @param value
     * @return Numero formatado como '#.##0,##'
     */
    public static String formatDisplayNumber(int value) {
        return nf.format(value).replace('.', ';').replace(',', '.').replace(';', ',');
    }

    private final static TreeMap<Integer, String> ROMAN_MAP = new TreeMap<Integer, String>();

    static {

        ROMAN_MAP.put(1000, "M");
        ROMAN_MAP.put(900, "CM");
        ROMAN_MAP.put(500, "D");
        ROMAN_MAP.put(400, "CD");
        ROMAN_MAP.put(100, "C");
        ROMAN_MAP.put(90, "XC");
        ROMAN_MAP.put(50, "L");
        ROMAN_MAP.put(40, "XL");
        ROMAN_MAP.put(10, "X");
        ROMAN_MAP.put(9, "IX");
        ROMAN_MAP.put(5, "V");
        ROMAN_MAP.put(4, "IV");
        ROMAN_MAP.put(1, "I");

    }

}
