package gq.zunarmc.spigot.floatingpets.manager.menu;

import gq.zunarmc.spigot.floatingpets.FloatingPets;
import gq.zunarmc.spigot.floatingpets.menu.model.Menu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuManager {

    private final Map<UUID, Menu> openedMenus = new HashMap<>();

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

}