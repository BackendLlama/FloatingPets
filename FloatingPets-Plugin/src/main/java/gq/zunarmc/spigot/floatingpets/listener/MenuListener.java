package gq.zunarmc.spigot.floatingpets.listener;

import gq.zunarmc.spigot.floatingpets.FloatingPets;
import gq.zunarmc.spigot.floatingpets.menu.model.Menu;
import gq.zunarmc.spigot.floatingpets.menu.model.MenuItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class MenuListener implements Listener {

    private final FloatingPets plugin;
    public MenuListener(FloatingPets plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(!(event.getWhoClicked() instanceof Player))
            return;

        Player player = (Player) event.getWhoClicked();
        Menu openedMenu = plugin.getMenuManager().getOpenedMenu(player.getUniqueId());

        if(openedMenu == null) {
            return;
        }

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if(clickedItem == null)
            return;

        Optional<MenuItem> menuItem = openedMenu.getItemByStack(clickedItem, event.getSlot());

        if(!menuItem.isPresent()) {
            return;
        }

        menuItem.get().onClick(player);

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        if(!(event.getPlayer() instanceof Player))
            return;

        Player player = (Player) event.getPlayer();

        Menu menu = plugin.getMenuManager().getOpenedMenu(player.getUniqueId());
        if(menu == null)
            return;

        plugin.getMenuManager().clearMenu(player.getUniqueId());

    }

}