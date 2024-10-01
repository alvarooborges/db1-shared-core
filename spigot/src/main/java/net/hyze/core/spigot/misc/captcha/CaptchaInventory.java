package net.hyze.core.spigot.misc.captcha;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.material.MaterialData;

public class CaptchaInventory extends CustomInventory {

    private static final Integer[] SLOTS = new Integer[]{10, 12, 14, 16};

    public CaptchaInventory(Runnable successRunnable, Runnable failRunnable) {
        this(
                CaptchaItem.values()[new Random().nextInt(CaptchaItem.values().length)],
                Maps.newLinkedHashMap(),
                successRunnable,
                failRunnable);
    }
    
    public CaptchaInventory(CaptchaItem enumCaptchaItem,
            LinkedHashMap<MaterialData, Boolean> items,
            Runnable successRunnable,
            Runnable failRunnable) {

        super(27, "Clique " + enumCaptchaItem.getPhrase() + ".");

        boolean reopening = !items.isEmpty();

        if (!reopening) {

            List<MaterialData> mustClickItems = Lists.newArrayList(enumCaptchaItem.getItems());

            mustClickItems.forEach(materialData -> items.put(materialData, false));

            while (items.size() < SLOTS.length) {

                CaptchaItem randomEnumCaptchaItem = CaptchaItem.values()[new Random().nextInt(CaptchaItem.values().length)];
                MaterialData materialData = randomEnumCaptchaItem.getRandomItem();
                
                if(mustClickItems.stream().anyMatch(item -> item.getItemType() == materialData.getItemType()) || enumCaptchaItem.getConflicts().contains(materialData)) {
                    continue;
                }

                items.put(materialData, false);

            }

        }

        if (!reopening) {

            List<Entry<MaterialData, Boolean>> list = Lists.newArrayList(items.entrySet());

            Collections.shuffle(list);

            items.clear();
            list.stream().forEach(entry -> items.put(entry.getKey(), entry.getValue()));

        }

        int slotIndex = 0;

        for (Entry<MaterialData, Boolean> entry : items.entrySet()) {

            boolean selected = entry.getValue();
            MaterialData materialData = entry.getKey();
            ItemBuilder itemCustom = new ItemBuilder(materialData.getItemType());

            itemCustom.durability(materialData.getData()).flags(ItemFlag.values()).name("&bItem");

            if (selected) {
                itemCustom.glowing(true).lore("Selecionado.");
            } else {
                itemCustom.lore("Clique para selecionar.");
            }

            setItem(SLOTS[slotIndex++], itemCustom.make(), (InventoryClickEvent event) -> {
                
                Player player = (Player) event.getWhoClicked();

                if (selected) {
                    items.put(materialData, false);
                } else {
                    
                    if(!enumCaptchaItem.getItems().contains(materialData)) {
                        player.closeInventory();
                        failRunnable.run();
                        return;
                    }

                    items.put(materialData, true);

                    List<MaterialData> selectedItems = items.entrySet().stream()
                            .filter(entry_ -> entry_.getValue())
                            .map(Entry::getKey)
                            .collect(Collectors.toList());

                    if (selectedItems.size() == enumCaptchaItem.getItems().size()) {

                        player.closeInventory();
                        
                        if (equals(selectedItems, enumCaptchaItem.getItems())) {
                            successRunnable.run();
                        } else {
                            failRunnable.run();
                        }

                        return;
                    }

                }

                player.openInventory(new CaptchaInventory(enumCaptchaItem, items, successRunnable, failRunnable));

            });

        }

    }

    private static boolean equals(List<MaterialData> list1, List<MaterialData> list2) {

        if (list1.size() != list2.size()) {
            return false;
        }

        return !list1.stream().anyMatch(materialData -> !list2.contains(materialData));

    }

}
