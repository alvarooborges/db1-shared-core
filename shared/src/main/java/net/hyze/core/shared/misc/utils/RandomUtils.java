package net.hyze.core.shared.misc.utils;

import net.hyze.core.shared.CoreConstants;
import java.util.List;

public class RandomUtils {

    public static <T> T randomKey(List<T> list) {
        return list.get(CoreConstants.RANDOM.nextInt(list.size()));
    }

    public static <T> T randomKey(T... args) {
        return args[CoreConstants.RANDOM.nextInt(args.length)];
    }

    public static int randomInt(int start, int end) {
        return CoreConstants.RANDOM.nextInt(Math.abs(end - start) + 1) + Math.min(start, end);
    }

}
