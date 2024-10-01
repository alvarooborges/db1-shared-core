package net.hyze.core.shared.misc.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import javax.imageio.ImageIO;

public class ImageUtils {

    public static BufferedImage getImage(URL url) throws IOException {
        URLConnection uc = url.openConnection();
        uc.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
        uc.connect();
        uc.getInputStream();
        BufferedInputStream in = new BufferedInputStream(uc.getInputStream());
        return ImageIO.read(in);
    }

}
