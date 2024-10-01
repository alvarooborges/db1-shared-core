package net.hyze.core.spigot.misc.utils;

import net.hyze.core.shared.misc.utils.Patterns;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;

@RequiredArgsConstructor
public enum BannerAlphabet {

    A('A', "ls-rs-ts-ms-&bo"),
    B('B', "rs-bs-ms-ts-cbo-bl-tl-vh-&bo"),
    C('C', "ts-bs-rs-&ms-vh-&bo"),
    D('D', "rs-bs-ts-&cbo-vh-&bo"),
    E('E', "ms-bs-ts-vh-&bo"),
    F('F', "ls-ts-ms-&bo"),
    G('G', "rs-ms-&hh-&vh-bs-ls-ts-&bo"),
    H('H', "ms-ls-rs-&bo"),
    I('I', "bs-ts-cs-&bo"),
    J('J', "bl-cs-ts-&bo"),
    K('K', "dls-drs-&bl-&tl-ls-&bo"),
    L('L', "bs-vh-&bo"),
    M('M', "tt-ls-rs-&bo-&tts"),
    N('N', "ls-drs-rs-&bo"),
    O('O', "ls-rs-bs-ts-&bo"),
    P('P', "rs-ms-&hhb-ls-ts-&bo"),
    Q('Q', "ls-rs-ts-&bs-bt-&bo"),
    R('R', "hh-drs-&vh-ls-ts-ms-&bo"),
    S('S', "bs-ts-drs-&bo"),
    T('T', "cs-ts-&bo"),
    U('U', "ls-rs-bs-&bo"),
    V('V', "ls-&bl-dls-&bo"),
    W('W', "bt-ls-rs-&bo-&bts"),
    X('X', "dls-drs-&bo"),
    Y('Y', "drs-&rd-dls-&bo"),
    Z('Z', "ts-bs-dls-&bo"),
    CHAR91('[', "ls-rs-ts-ms-&bo"), // fix pattern
    CHAR92('\\', "ls-rs-ts-ms-&bo"),// fix pattern
    CHAR93(']', "ls-rs-ts-ms-&bo"), // fix pattern
    CHAR94('^', "ls-rs-ts-ms-&bo"), // fix pattern
    CHAR95('_', "ls-rs-ts-ms-&bo"), // fix pattern
    ZERO('0', "vh-vhr-&cs-ts-bs-&bo"),
    ONE('1', "tl-&tts-cs-&bo"),
    TWO('2', "ts-&flo-dls-&cbo-bs-&bo"),
    THREE('3', "ms-&ls-rs-ts-bs-&bo"),
    FOUR('4', "&hh-ls-&bs-rs-ms-&bo"),
    FIVE('5', "bs-drs-&cbo-bl-ts-&bo"),
    SIX('6', "rs-&hh-bs-ms-ts-&bo"), //new
    SEVEN('7', "ts-&rd-dls-bl-&bo"),
    EIGHT('8', "rs-&ts-bs-ms-ls-ts-rs-&bo"),
    NINE('9', "ls-&hhb-ms-ts-rs-bs-&bo-&bo");

    private final char character;

    private final String code;

    public List<Pattern> buildPatterns(DyeColor backgroundColor, DyeColor fillColor) {
        return Arrays.stream(Patterns.HYPHEN.split(code)).map((String arg) -> {
            boolean base = arg.contains("&");
            String parse = base ? Patterns.COMERCIAL_E.replace(arg, "") : arg;
            return new Pattern(base ? backgroundColor : fillColor, PatternType.getByIdentifier(parse));
        }).collect(Collectors.toCollection(LinkedList::new));
    }

    /*
     * 
     */
    private static final LinkedHashMap<Character, BannerAlphabet> PARSE_TO_BANNER = new LinkedHashMap<>();

    static {
        for (BannerAlphabet b : values()) {
            PARSE_TO_BANNER.put(b.character, b);
        }
    }

    public static BannerAlphabet getBanner(char letter) {
        return PARSE_TO_BANNER.get(letter);
    }
}
