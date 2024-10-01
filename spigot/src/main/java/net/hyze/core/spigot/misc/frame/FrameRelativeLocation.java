package net.hyze.core.spigot.misc.frame;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class FrameRelativeLocation {

    private final int x, y;

    @Override
    public String toString() {
        return x + ":" + y;
    }

    public static FrameRelativeLocation fromString(String str) {

        String[] args = str.split(":");

        if (args.length != 2) {
            return null;
        }

        int x;
        int y;

        try {
            x = Integer.parseInt(args[0]);
            y = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {
            return null;
        }

        return new FrameRelativeLocation(x, y);
    }

}
