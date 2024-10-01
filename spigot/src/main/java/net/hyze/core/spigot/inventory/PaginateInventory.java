package net.hyze.core.spigot.inventory;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Maps;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

@Setter
public class PaginateInventory extends CustomInventory {

    private LinkedListMultimap<Integer, Icon> items = LinkedListMultimap.create();
    private LinkedHashMap<Integer, Icon> menu = Maps.newLinkedHashMap();

    private ItemStack emptyIcon = new ItemBuilder(Material.WEB).name("&cVazio").make();
    private int page = 0;
    private boolean defaultHotbar = true;
    private Supplier<CustomInventory> backInventory;

    private int buildPage = 0;

    private static final Integer[] SLOTS = {
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34
    };

    private PaginateInventory(String title, int rows, int page, boolean defaultHotbar, LinkedHashMap<Integer, Icon> menu, LinkedListMultimap<Integer, Icon> items, Supplier<CustomInventory> backInventory) {
        super(rows * 9, title);

        this.page = page;
        this.defaultHotbar = defaultHotbar;
        this.menu = menu;
        this.items = items;
        this.backInventory = backInventory;
    }

    protected PaginateInventory(String title) {
        super(54, title);
    }

    @Override
    public void onOpen(InventoryOpenEvent event0) {

        int lines;

//        if (items.size() <= 7) {
//            lines = 3;
//        } else if (items.size() <= 14) {
//            lines = 3;
//        } else if (items.size() <= 21) {
//            lines = 4;
//        } else {
//            lines = 5;
//        }
//
//        if (!menu.isEmpty()) {
//            lines += 1;
//        }
//
//        ((MinecraftInventory) this.inventory).items = new net.minecraft.server.v1_8_R3.ItemStack[lines * 9];
        AtomicInteger slot = new AtomicInteger(0);

        menu.forEach((menuSlot, menuIcon) -> {
            super.setItem(menuSlot, menuIcon.getIcon(), menuIcon.getInventoryClick());
        });

        int totalPages = 1;

        if (!items.isEmpty()) {
            totalPages = (int) Math.ceil((double) items.size() / (double) SLOTS.length);
        }

        if (items.values().isEmpty()) {
            super.setItem(22, emptyIcon);
        } else {
            items.get(page).forEach(icon -> {
                super.setItem(SLOTS[slot.getAndIncrement()], icon.getIcon(), icon.getInventoryClick());
            });
        }

        if (backInventory != null) {
            ItemBuilder backItem = new ItemBuilder(Material.ARROW).name("&aVoltar");
            super.setItem(getSize() - 5, backItem.make(), event -> event.getWhoClicked().openInventory(backInventory.get()));
        }

        if (!defaultHotbar) {
            return;
        }

        /**
         * Caso não seja a primeira página, é colocado o botão de voltar.
         */
        if (page > 0) {
            super.setItem(getSize() - 9,
                    new ItemBuilder(Material.ARROW).name("&aPágina Anterior: " + (page - 1)).make(),
                    event -> {
                        event.getWhoClicked().openInventory(new PaginateInventory(getTitle(), getSize() / 9, page - 1, defaultHotbar, menu, items, backInventory));
                    });
        }

        /**
         * Icone que informa qual página esta.
         */
//        if (backInventory == null && totalPages > 1) {
//            super.setItem(
//                    getSize() - 5,
//                    new ItemBuilder(Material.PAPER).name("&aPágina &f" + (page + 1) + "/" + totalPages).amount(page + 1).make()
//            );
//        }
        /**
         * Caso não seja a última página, é colocado o botão de avançar.
         */
        if (page < (totalPages - 1)) {
            super.setItem(getSize() - 1,
                    new ItemBuilder(Material.ARROW).name("&aPróxima Página: " + (page + 1)).make(),
                    event -> {
                        event.getWhoClicked().openInventory(new PaginateInventory(getTitle(), getSize() / 9, page + 1, defaultHotbar, menu, items, backInventory));
                    });
        }

        super.onOpen(event0);
    }

    public void addMenu(Integer slot, ItemStack icon, Consumer<InventoryClickEvent> inventoryClick) {
        this.menu.put(slot, new PaginateInventory.Icon(icon, inventoryClick));
    }

    public void addMenu(Integer slot, ItemStack icon) {
        this.menu.put(slot, new PaginateInventory.Icon(icon));
    }

    @Override
    public void addItem(ItemStack icon, Consumer<InventoryClickEvent> inventoryClick) {
        if (this.items.containsKey(this.buildPage) && this.items.get(this.buildPage).size() == SLOTS.length) {
            this.buildPage++;
        }

        this.items.put(this.buildPage, new PaginateInventory.Icon(icon, inventoryClick));
    }

    @Override
    public void addItem(ItemStack icon, Runnable inventoryClick) {
        addItem(icon, event -> inventoryClick.run());
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        super.onClick(event);

        if (event.getClickedInventory() == null && backInventory != null) {
            event.getWhoClicked().openInventory(backInventory.get());
        }
    }

    @Override
    public HashMap<Integer, ItemStack> addItem(ItemStack... icon) {
        for (ItemStack stack : icon) {
            if(stack == null) {
                continue;
            }

            this.addItem(stack, (Consumer) null);
        }

        return Maps.newHashMap();
    }

    public static PaginateInventoryBuilder builder() {
        return new PaginateInventoryBuilder();
    }

    @Getter
    @RequiredArgsConstructor
    @AllArgsConstructor
    private static class Icon {

        private final ItemStack icon;
        private Consumer<InventoryClickEvent> inventoryClick;

    }

    public static class PaginateInventoryBuilder {

        private final LinkedListMultimap<Integer, PaginateInventory.Icon> items = LinkedListMultimap.create();
        private final LinkedHashMap<Integer, PaginateInventory.Icon> menu = Maps.newLinkedHashMap();

        private ItemStack emptyIcon = new ItemBuilder(Material.WEB).name("&cVazio").make();
        private int page = 0;
        private boolean defaultHotbar = true;
        private Supplier<CustomInventory> backInventory;

        public PaginateInventoryBuilder item(ItemStack icon, Consumer<InventoryClickEvent> inventoryClick) {
            if (this.items.containsKey(this.page) && this.items.get(this.page).size() == SLOTS.length) {
                this.page++;
            }

            this.items.put(this.page, new PaginateInventory.Icon(icon, inventoryClick));
            return this;
        }

        public PaginateInventoryBuilder menu(Integer slot, ItemStack icon, Consumer<InventoryClickEvent> inventoryClick) {
            this.menu.put(slot, new PaginateInventory.Icon(icon, inventoryClick));
            return this;
        }

        public PaginateInventoryBuilder emptyIcon(ItemStack emptyIcon) {
            this.emptyIcon = emptyIcon;
            return this;
        }

        public PaginateInventoryBuilder backInventory(Supplier<CustomInventory> backInventory) {
            this.backInventory = backInventory;
            return this;
        }

        /**
         * Caso seja true, os icones de voltar e avançar estarão sempre visiveis
         * na última linha do inventário.
         *
         * @param value
         * @return
         */
        public PaginateInventoryBuilder defaultHotbar(boolean value) {
            this.defaultHotbar = value;
            return this;
        }

        public PaginateInventory build(String title) {
            int rows = this.items.size() > SLOTS.length ? 6 : 5;
            return new PaginateInventory(title, rows, 0, this.defaultHotbar, this.menu, this.items, this.backInventory);
        }
    }
}
