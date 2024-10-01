package net.hyze.core.spigot.inventory;

import lombok.Getter;

public class Container {

    @Getter
    protected ICustomInventory parent;

    public void init(ICustomInventory parent) {
        this.parent = parent;
    }

}