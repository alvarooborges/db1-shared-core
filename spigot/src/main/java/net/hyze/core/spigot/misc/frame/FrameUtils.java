package net.hyze.core.spigot.misc.frame;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.map.MapView;

public class FrameUtils {

    public static BufferedImage resize(Image img, Integer width, Integer height) {

        img = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return bimage;
    }

    public static String imgToBase64String(final RenderedImage img, final String formatName) {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, formatName, Base64.getEncoder().wrap(os));
            return os.toString(StandardCharsets.ISO_8859_1.name());
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    public static BufferedImage base64StringToImg(final String base64String) {
        try {
            return ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(base64String)));
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    @SuppressWarnings("deprecation")
    public static short getMapID(MapView map) {
        return map.getId();
    }

    @SuppressWarnings("deprecation")
    public static MapView getMapView(short id) {
        MapView map = Bukkit.getMap(id);
        if (map != null) {
            return map;
        }

        return Bukkit.createMap(getDefaultWorld());
    }

    public static World getDefaultWorld() {
        return Bukkit.getWorlds().get(0);
    }

    public static Integer getPanes(int size) {
        while (size % 128 != 0) {
            size++;
        }

        return size / 128;
    }
}
