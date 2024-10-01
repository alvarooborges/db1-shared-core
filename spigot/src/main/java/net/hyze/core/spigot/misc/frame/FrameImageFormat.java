package net.hyze.core.spigot.misc.frame;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FrameImageFormat {

    PNG("png"),
    JPEG("jpg");

    private final String extension;

    public static FrameImageFormat fromExtension(String extension) {
        for (FrameImageFormat imageFormat : values()) {
            if (imageFormat.getExtension().equalsIgnoreCase(extension)) {
                return imageFormat;
            }
        }
        return null;
    }
}
