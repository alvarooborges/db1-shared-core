package net.hyze.core.spigot.misc.hologram;

import com.google.common.collect.Lists;
import java.util.LinkedList;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

@RequiredArgsConstructor
public class Hologram {

    private final LinkedList<HologramLine> lines = Lists.newLinkedList();
    private final HologramPosition hologramPosition;

    public Hologram line(String text) {
        lines.add(new HologramLine().text(text));
        return this;
    }

    public Hologram line(int position, String text) {
        lines.add(position, new HologramLine().text(text));
        return this;
    }

    public void update(int position, String text) {
        if (this.lines.get(position) != null) {
            this.lines.get(position).update(text);
        }
    }

    public void update() {
        this.lines.forEach(HologramLine::update);
    }

    public void spawn(Location location) {
        location.getChunk().load();

        Location hologramLocation = location.clone();

        for (HologramLine hologramLine : this.lines) {
            hologramLine.spawn(hologramLocation);
            hologramLocation = hologramLocation.add(0, this.hologramPosition.getValue(), 0);
        }
    }

    public void destroy() {
        this.lines.forEach(HologramLine::destroy);
    }

    public void teleport(Location loc) {
        Location location = loc.clone();

        for (HologramLine hologramLine : this.lines) {
            hologramLine.getArmorStand().teleport(location);
            location = location.add(0, this.hologramPosition.getValue(), 0);
        }
    }
}
