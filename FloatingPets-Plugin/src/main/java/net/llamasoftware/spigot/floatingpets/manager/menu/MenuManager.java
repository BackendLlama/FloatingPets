package net.llamasoftware.spigot.floatingpets.manager.menu;

import net.llamasoftware.spigot.floatingpets.FloatingPets;
import net.llamasoftware.spigot.floatingpets.api.model.PetCategory;
import net.llamasoftware.spigot.floatingpets.api.model.PetType;
import net.llamasoftware.spigot.floatingpets.menu.MenuPetSelector;
import net.llamasoftware.spigot.floatingpets.menu.model.Menu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class MenuManager {

    private final Map<UUID, Menu> openedMenus = new HashMap<>();
    private final FloatingPets plugin;

    public MenuManager(FloatingPets plugin) {
        this.plugin = plugin;
    }

    public Menu getOpenedMenu(UUID player){
        return openedMenus.get(player);
    }

    public void openMenu(Player player, Menu menu, FloatingPets plugin) {
        player.closeInventory();

        openedMenus.put(player.getUniqueId(), menu);
        menu.open(player, this, plugin);
    }

    public void clearMenu(UUID player) {
        openedMenus.remove(player);
    }

    public void openPetSelector(Player player, PetCategory category){
        List<PetType> types = plugin.getStorageManager().getCachedTypes()
                .stream()
                .filter(type -> type.getCategory().equals(category))
                .filter(type -> player.hasPermission(type.getPermission()))
                .collect(Collectors.toList());

        MenuPetSelector menu = new MenuPetSelector(plugin.getStorageManager().getLocaleByKey("menus.selector.title"), types);
        plugin.getMenuManager().openMenu(player, menu, plugin);
    }

}