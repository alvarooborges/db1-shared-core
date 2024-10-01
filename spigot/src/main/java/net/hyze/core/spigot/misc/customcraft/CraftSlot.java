package net.hyze.core.spigot.misc.customcraft;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

@AllArgsConstructor
public class CraftSlot {

    public final int slot;
    @Getter
    public final MaterialData materialData;
    @Getter
    public final int amount;

    public char getSlot() {
        return (char) (slot + '0');
    }

    public int getIntSlot() {
        return slot;
    }

    public CraftSlot(int slot, Material material) {
        this.slot = slot;
        this.materialData = new MaterialData(material, (byte) 0);
        this.amount = 1;
    }

    public CraftSlot(int slot, MaterialData materialData) {
        this.slot = slot;
        this.materialData = materialData;
        this.amount = 1;
    }

    public CraftSlot(Material material) {
        this.slot = 0;
        this.materialData = new MaterialData(material, (byte) 0);
        this.amount = 1;
    }

    public CraftSlot(MaterialData materialData) {
        this.slot = 0;
        this.materialData = materialData;
        this.amount = 1;
    }

}
