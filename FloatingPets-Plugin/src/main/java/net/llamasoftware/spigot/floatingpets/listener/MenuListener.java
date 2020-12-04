package net.llamasoftware.spigot.floatingpets.listener;

import net.llamasoftware.spigot.floatingpets.FloatingPets;
import net.llamasoftware.spigot.floatingpets.menu.model.Menu;
import net.llamasoftware.spigot.floatingpets.menu.model.MenuItem;
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

        if(!openedMenu.isModifiable())
            event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if(clickedItem == null)
            return;

        Optional<MenuItem> menuItem = openedMenu.getItemByStack(event.getSlot());

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

        menu.onClose(player);

        plugin.getMenuManager().clearMenu(player.getUniqueId());
    }

}