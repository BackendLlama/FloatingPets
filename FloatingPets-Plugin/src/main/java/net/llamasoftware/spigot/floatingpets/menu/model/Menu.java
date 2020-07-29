package net.llamasoftware.spigot.floatingpets.menu.model;

import net.llamasoftware.spigot.floatingpets.FloatingPets;
import net.llamasoftware.spigot.floatingpets.manager.menu.MenuManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class Menu {

    @Getter @Setter
    private String title;
    @Getter @Setter
    private int rows;

    @Getter
    private MenuManager menuManager;
    @Getter
    private FloatingPets plugin;

    private final Map<String, Object> data = new HashMap<>();

    public Menu(String title, int rows) {
        this.title = title;
        this.rows = rows;
    }

    public Optional<MenuItem> getItemByStack(ItemStack stack, int slot){
        return getItems().getAll().stream()
                .filter(item -> isSimilar(stack, item, slot))
                .findAny();
    }

    private boolean isSimilar(ItemStack stack, MenuItem other, int slot){
        if(stack == null || other == null)
            return false;

        if(other.getSlot() != slot)
            return false;

        boolean initial = other.getStack().getType() == stack.getType()
                && other.getStack().getAmount() == stack.getAmount();

        if(!initial)
            return false;

        if(!(stack.hasItemMeta() && other.getStack().hasItemMeta())){
            return true;
        }

        return stack.getItemMeta().hasDisplayName() && other.getStack().getItemMeta().hasDisplayName()
                && stack.getItemMeta().getDisplayName().equals(other.getStack().getItemMeta().getDisplayName());
    }

    public void open(Player player, MenuManager menuManager, FloatingPets plugin){
        if(!(rows > 0 && rows < 7))
            return;

        this.menuManager = menuManager;
        this.plugin = plugin;

        Inventory inventory = Bukkit.createInventory(null, rows * 9, title);
        getItems().getAll()
                .forEach(item -> inventory.setItem(item.getSlot(), item.getStack()));

        player.openInventory(inventory);
    }

    public abstract MenuItemRepository getItems();

    public void setData(String key, Object object){
        data.put(key, object);
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(String key, Class<T> type){
        return type.cast(data.get(key));
    }

    public Object getData(String key){
        return data.get(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Menu menu = (Menu) o;
        return rows == menu.rows &&
                Objects.equals(title, menu.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, rows);
    }

}