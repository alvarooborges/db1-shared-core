package net.hyze.core.spigot.misc.enchantments.merchant;

import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.IMerchant;

public class InventoryMerchant extends net.minecraft.server.v1_8_R3.InventoryMerchant {

    public InventoryMerchant(EntityHuman entityhuman, IMerchant imerchant) {
        super(entityhuman, imerchant);
    }

}
