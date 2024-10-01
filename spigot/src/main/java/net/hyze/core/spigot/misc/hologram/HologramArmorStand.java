package net.hyze.core.spigot.misc.hologram;

import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.World;

public class HologramArmorStand extends EntityArmorStand {

    public HologramArmorStand(World world) {
        super(world);

        this.setInvisible(true);
        this.setArms(false);
        this.setGravity(true);
        this.setBasePlate(false);
        this.setSmall(true);
        this.setCustomNameVisible(true);
        this.noclip = true;
        this.n(true);
    }

    @Override
    public boolean d(int i, ItemStack itemstack) {
        return false;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        return false;
    }
}
