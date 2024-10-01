package net.hyze.core.spigot.misc.utils;

import com.google.common.collect.Maps;
import net.hyze.core.shared.misc.utils.NumberUtils;
import net.hyze.core.spigot.CoreSpigotPlugin;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.server.v1_8_R3.MobEffectList;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class TranslateItem {

    private static final Map<String, String> TRANSLATIONS = Maps.newHashMap();

    public TranslateItem() {
        Pattern pattern = Pattern.compile("^\\s*([\\w\\d\\.]+)\\s*=\\s*(.*)\\s*$");
        
        try {
            InputStream fis = CoreSpigotPlugin.getInstance().getResource("pt_BR.lang");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            Matcher matcher;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.contains("=")) {
                    matcher = pattern.matcher(line);
                    if (matcher.matches()) {
                        TRANSLATIONS.put(matcher.group(1), matcher.group(2));
                    }
                }

            }
        } catch (IOException ex) {
        }
    }

    public String get(ItemStack itemStack) {
        return get(itemStack, false);
    }
    
    public String get(ItemStack itemStack, boolean amount) {
        net.minecraft.server.v1_8_R3.ItemStack nms = CraftItemStack.asNMSCopy(itemStack);
        String node = (nms == null ? "tile.air" : nms.a()) + ".name";

        String val = TRANSLATIONS.get(node);

        if (val == null) {
            return node;
        }

        return (amount ? itemStack == null ? 1 : itemStack.getAmount() + "x " : "") + val;
    }

    public String get(Enchantment enchantment) {
        net.minecraft.server.v1_8_R3.Enchantment nms = net.minecraft.server.v1_8_R3.Enchantment.getById(enchantment.getId());
        String node = nms == null ? enchantment.getName() : nms.a();

        String val = TRANSLATIONS.get(node);

        if (val == null) {
            return node;
        }

        return val;
    }

    public String get(PotionEffect potionEffect) {
        String name = TRANSLATIONS.get((String) getFieldValue(MobEffectList.byId[potionEffect.getType().getId()], "M"));

        int value = potionEffect.getDuration() / 20;
        int minutes = value / 60;
        int seconds = value - (minutes * 60);

        String time = String.format("%02d:%02d", minutes, seconds);

        return name + (potionEffect.getAmplifier() >= 0 ? " " + NumberUtils.toRoman(potionEffect.getAmplifier() + 1) : "") + " - " + time;
    }

    private <T> T getFieldValue(Object obj, String fieldname) {
        Class<?> clazz = obj.getClass();
        do {
            try {
                Field field = clazz.getDeclaredField(fieldname);
                field.setAccessible(true);
                return (T) field.get(obj);
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException t) {
            }
        } while ((clazz = clazz.getSuperclass()) != null);
        return null;
    }

}
