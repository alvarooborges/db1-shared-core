package net.hyze.core.spigot.misc.enchantments.merchant;

import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.PlayerInventory;
import net.minecraft.server.v1_8_R3.World;

public class ContainerMerchant extends net.minecraft.server.v1_8_R3.ContainerMerchant {

    private final Merchant merchant;

    public ContainerMerchant(PlayerInventory playerinventory, Merchant merchant, World world) {
        super(playerinventory, merchant, world);
        this.merchant = merchant;
    }

    @Override
    public ItemStack b(EntityHuman entityhuman, int i) {
        return super.b(entityhuman, i);
    }

    @Override
    public void b(EntityHuman entityhuman) {
        if (this.e().getContents()[0] != null) {
            entityhuman.drop(this.e().getContents()[0], false);
        }

        if (this.e().getContents()[1] != null) {
            entityhuman.drop(this.e().getContents()[1], false);
        }

        this.e().getContents()[0] = null;
        this.e().getContents()[1] = null;

        super.b(entityhuman);
    }
}
