package net.hyze.core.spigot.misc.customcraft;

import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Recipe;
import org.bukkit.material.MaterialData;

public class CustomCraft {

    protected static final HashSet<Craft> CUSTOM_RECIPES = Sets.newHashSet();

    public static void insertCraft(Craft craft) {
        CUSTOM_RECIPES.add(craft);
    }

    public static void removeDefaultRecipes(Material... materials) {
        HashSet<Material> overrides = Sets.newHashSet(materials);
        Iterator<Recipe> it = Bukkit.getServer().recipeIterator();
        Recipe recipe;

        while (it.hasNext()) {
            recipe = it.next();
            if (recipe != null && overrides.contains(recipe.getResult().getType())) {
                it.remove();
            }
        }
    }

    public static void removeDefaultRecipe(MaterialData materialData) {
        Iterator<Recipe> it = Bukkit.getServer().recipeIterator();
        Recipe recipe;

        while (it.hasNext()) {
            recipe = it.next();

            if (recipe != null
                    && materialData.getItemType().equals(recipe.getResult().getType())
                    && materialData.getData() == recipe.getResult().getDurability()) {
                it.remove();
            }
        }
    }

    public static void buildRecipes() {
        HashSet<Material> overrides = Sets.newHashSet();

        CUSTOM_RECIPES.stream().filter(craft -> craft.isOverrideDefault()).forEach(craft -> overrides.add(craft.getResult().type()));

        if (!overrides.isEmpty()) {
            removeDefaultRecipes(overrides.stream().toArray(Material[]::new));
        }

        CUSTOM_RECIPES.forEach(custom -> {
            if (!Bukkit.getServer().addRecipe(custom.build())) {
                System.out.println("Recipe: " + custom.getResult().type() + " was NOT added.");
            }
        });
    }

}
