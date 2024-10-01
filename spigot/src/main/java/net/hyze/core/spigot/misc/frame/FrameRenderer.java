package net.hyze.core.spigot.misc.frame;

import java.awt.image.BufferedImage;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FrameRenderer extends MapRenderer {

    private final BufferedImage image;
    private Boolean imageRendered = false;

    @Override
    public void render(MapView view, MapCanvas canvas, Player player) {

        canvas.setCursors(new MapCursorCollection());

        if (imageRendered) {
            return;
        }

        canvas.drawImage(0, 0, image);
        this.imageRendered = true;
    }
}
