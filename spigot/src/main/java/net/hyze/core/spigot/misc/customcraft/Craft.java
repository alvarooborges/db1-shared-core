package net.hyze.core.spigot.misc.customcraft;

import com.google.common.collect.Sets;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import lombok.Getter;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

@Getter
public class Craft {

    public final HashSet<CraftSlot> slots = Sets.newHashSet();
    public final ItemBuilder result;
    public final Class<? extends Recipe> recipeClass;
    public final boolean overrideDefault;

    /**
     * @param result
     * @param recipeClass needs to be ShapelessRecipe || ShapedRecipe.class
     * @param overrideDefault removes all defaults recipes for that
     * resuilt.getType()
     * @param slots
     */
    public Craft(ItemBuilder result, Class<? extends Recipe> recipeClass, boolean overrideDefault, CraftSlot... slots) {
        this.result = result;
        this.overrideDefault = overrideDefault;
        this.recipeClass = (recipeClass.equals(ShapedRecipe.class) || recipeClass.equals(ShapelessRecipe.class)) ? recipeClass : ShapelessRecipe.class;
        if (slots != null) {
            this.slots.addAll(Arrays.asList(slots));
        }
    }

    /**
     * @param result
     * @param recipeClass needs to be ShapelessRecipe || ShapedRecipe.class
     * @param slots
     */
    public Craft(ItemBuilder result, Class<? extends Recipe> recipeClass, CraftSlot... slots) {
        this.result = result;
        this.overrideDefault = false;
        this.recipeClass = (recipeClass.equals(ShapedRecipe.class) || recipeClass.equals(ShapelessRecipe.class)) ? recipeClass : ShapelessRecipe.class;

        if (slots != null) {
            this.slots.addAll(Arrays.asList(slots));
        }
    }

    public <T extends Recipe> T build() {
        if (recipeClass == null) {
            return null;
        }
        try {
            T recipe = (T) recipeClass.getConstructors()[0].newInstance(result.make());

            if (recipe instanceof ShapedRecipe) {
                ((ShapedRecipe) recipe).shape("012", "345", "678");

                slots.stream().filter(slot -> slot.getIntSlot() >= 0 && slot.getIntSlot() < 9)
                        .forEach(slot -> ((ShapedRecipe) recipe).setIngredient(slot.getSlot(), slot.getMaterialData()));
            } else {
                slots.forEach(slot -> ((ShapelessRecipe) recipe).addIngredient(slot.getAmount(), slot.getMaterialData()));
            }

            return (T) recipe;
        } catch (IllegalArgumentException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

}
