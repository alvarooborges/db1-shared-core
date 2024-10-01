package net.hyze.core.spigot.misc.alchemy;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Recipes {

    private static List<String> DEFAULT = Lists.newArrayList(
            "BLAZE_POWDER",
            "FERMENTED_SPIDER_EYE",
            "GHAST_TEAR",
            "GLOWSTONE_DUST",
            "GOLDEN_CARROT",
            "MAGMA_CREAM",
            "NETHER_STALK",
            "REDSTONE",
            "SPECKLED_MELON",
            "SPIDER_EYE",
            "SUGAR",
            "SULPHUR",
            "WATER_LILY",
            "RAW_FISH:3",
            "CARROT_ITEM",
            "SLIME_BALL",
            "QUARTZ",
            "RED_MUSHROOM",
            "APPLE",
            "ROTTEN_FLESH",
            "BROWN_MUSHROOM",
            "INK_SACK:0",
            "LONG_GRASS:2",
            "POISONOUS_POTATO",
            "GOLDEN_APPLE:0",
            "IRON_INGOT"
    );

    public static List<ItemStack> getIngredients() {
        List<ItemStack> out = Lists.newArrayList();
        load(out, DEFAULT);
        return out;
    }

    private static void load(List<ItemStack> ingredientList, List<String> ingredientStrings) {
        if (ingredientStrings != null && ingredientStrings.size() > 0) {
            for (String ingredientString : ingredientStrings) {
                ItemStack ingredient = loadIngredient(ingredientString);

                if (ingredient != null) {
                    ingredientList.add(ingredient);
                }
            }
        }
    }

    private static ItemStack loadIngredient(String ingredient) {
        if (ingredient == null || ingredient.isEmpty()) {
            return null;
        }

        String[] parts = ingredient.split(":");

        Material material = parts.length > 0 ? Material.getMaterial(parts[0]) : null;
        short data = parts.length > 1 ? Short.parseShort(parts[1]) : 0;

        if (material != null) {
            return new ItemStack(material, 1, data);
        }

        return null;
    }

    private static Map<Short, AlchemyPotion> potionMap = new HashMap<Short, AlchemyPotion>();

    public static AlchemyPotion getPotion(short durability) {
        return potionMap.get(durability);
    }
}
