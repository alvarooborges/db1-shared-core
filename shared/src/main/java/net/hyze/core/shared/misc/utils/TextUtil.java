package net.hyze.core.shared.misc.utils;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.hyze.core.shared.CoreConstants;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TextUtil {

    public static String join(Object[] parts, String delimiter, String lastDelimiter) {
        return join(
                Lists.newArrayList(parts).stream().map(Object::toString).collect(Collectors.toList()),
                delimiter,
                lastDelimiter
        );
    }

    public static String join(List<String> list, String delimiter, String lastDelimiter) {
        if (list.isEmpty()) {
            return new String();
        }

        if (list.size() == 1) {
            return list.get(0);
        }

        int last = list.size() - 1;

        return String.join(
                lastDelimiter,
                String.join(delimiter, list.subList(0, last)),
                list.get(last)
        );
    }

    public static List<String> extractUrls(String text) {
        List<String> out = Lists.newArrayList();

        Matcher matcher = CoreConstants.URL_PATTERN.matcher(text);

        while (matcher.find()) {
            out.add(text.substring(matcher.start(0), matcher.end(0)));
        }

        return out;
    }

    public static <T extends Collection<? super String>> T copyPartialMatches(@NonNull String token, @NonNull Iterable<String> originals, @NonNull T collection) throws UnsupportedOperationException, IllegalArgumentException {
        Iterator var4 = originals.iterator();

        while (var4.hasNext()) {
            String string = (String) var4.next();
            if (startsWithIgnoreCase(string, token)) {
                collection.add(string);
            }
        }

        return collection;
    }

    public static boolean startsWithIgnoreCase(@NonNull String string, @NonNull String prefix) throws IllegalArgumentException, NullPointerException {
        return string.length() >= prefix.length() && string.regionMatches(true, 0, prefix, 0, prefix.length());
    }
}
