package net.hyze.core.spigot.misc.enchantments;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.ItemArmor;
import net.minecraft.server.v1_8_R3.ItemAxe;
import net.minecraft.server.v1_8_R3.ItemBow;
import net.minecraft.server.v1_8_R3.ItemPickaxe;
import net.minecraft.server.v1_8_R3.ItemSpade;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.ItemSword;
import net.minecraft.server.v1_8_R3.ItemTool;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;

@RequiredArgsConstructor
public enum CustomEnchantmentSlot {

    ALL("Todos os itens"),
    WEAPON("Todas as armas"),
    ARMOR("Todas as armaduras"),
    TOOLS("Todas as ferramentas"),
    ARMOR_FEET("Botas"),
    ARMOR_LEGS("Calças"),
    ARMOR_TORSO("Peitorais"),
    ARMOR_HEAD("Capacetes"),
    BOW("Arcos"),
    SWORD("Espadas"),
    AXES("Machados"),
    PICKAXE("Picareta"),
    SPADE("Pá"),;

    @Getter
    private final String name;

    public boolean canEnchant(org.bukkit.inventory.ItemStack stack) {
        ItemStack nmsCopy = CraftItemStack.asNMSCopy(stack);

        Item item = nmsCopy.getItem();

        if (item instanceof ItemArmor) {
            if (this == ARMOR || this == ALL) {
                return true;
            } else {
                ItemArmor itemarmor = (ItemArmor) item;

                switch (itemarmor.b) {
                    case 0:
                        return this == ARMOR_HEAD || this == ALL;
                    case 1:
                        return this == ARMOR_LEGS || this == ALL;
                    case 2:
                        return this == ARMOR_TORSO || this == ALL;
                    case 3:
                        return this == ARMOR_FEET || this == ALL;
                    default:
                        return false;
                }
            }
        }

        if (item instanceof ItemSword) {
            return this == SWORD || this == WEAPON || this == ALL;
        }

        if (item instanceof ItemAxe) {
            return this == AXES || this == WEAPON || this == TOOLS || this == ALL;
        }

        if (item instanceof ItemPickaxe) {
            return this == PICKAXE || this == TOOLS || this == ALL;
        }

        if (item instanceof ItemSpade) {
            return this == SPADE || this == TOOLS || this == ALL;
        }

        if (item instanceof ItemTool) {
            return this == TOOLS || this == ALL;
        }

        if (item instanceof ItemBow) {
            return this == BOW || this == ALL;
        }

        return false;
    }
}
