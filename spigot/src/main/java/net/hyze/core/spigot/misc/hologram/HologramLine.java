package net.hyze.core.spigot.misc.hologram;

import lombok.Getter;
import net.hyze.core.shared.messages.MessageUtils;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.entity.CreatureSpawnEvent;

@Getter
public class HologramLine {

    private ArmorStand armorStand;
    private String text;

    public HologramLine text(String text) {
        this.text = text;
        return this;
    }

    public HologramLine update(String text) {
        this.text = text;
        this.update();
        return this;
    }

    public HologramLine update() {
        this.armorStand.setCustomName(MessageUtils.translateColorCodes(this.text));
        return this;
    }

    public void spawn(Location location) {

        net.minecraft.server.v1_8_R3.World world = ((CraftWorld) location.getWorld()).getHandle();

        HologramArmorStand hologramArmorStand = new HologramArmorStand(world);

        hologramArmorStand.setPosition(location.getX(), location.getY(), location.getZ());

        world.addEntity(hologramArmorStand, CreatureSpawnEvent.SpawnReason.CUSTOM);

        this.armorStand = (ArmorStand) hologramArmorStand.getBukkitEntity();

        this.update();
    }

    public void destroy() {
        this.armorStand.remove();
    }

}
