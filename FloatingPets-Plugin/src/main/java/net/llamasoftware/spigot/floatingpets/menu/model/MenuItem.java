package net.llamasoftware.spigot.floatingpets.menu.model;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class MenuItem {

    @Getter
    private final ItemStack stack;
    @Getter
    private final int slot;

    protected MenuItem(ItemStack stack, int slot) {
        this.stack = stack;
        this.slot = slot;
    }

    public abstract void onClick(Player player);

}