package net.llamasoftware.spigot.floatingpets.menu;

import net.llamasoftware.spigot.floatingpets.api.model.Pet;
import net.llamasoftware.spigot.floatingpets.manager.storage.StorageManager;
import net.llamasoftware.spigot.floatingpets.menu.model.Menu;
import net.llamasoftware.spigot.floatingpets.menu.model.MenuItem;
import net.llamasoftware.spigot.floatingpets.menu.model.MenuItemRepository;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuPetStorage extends Menu {

    public MenuPetStorage(String title, int rows, Pet pet) {
        super(title, rows);
        setData("pet", pet);
        setModifiable(true);
        setData("items", pet.getExtra().get("storage"));
    }

    @Override
    public void onClose(Player player) {
        Pet pet = getData("pet", Pet.class);
        pet.setExtra("storage", new ArrayList<>(Arrays.asList(player.getOpenInventory().getTopInventory().getContents())));
        getPlugin().getStorageManager().updatePet(pet, StorageManager.Action.EXTRA);
    }

    @SuppressWarnings("unchecked")
    @Override
    public MenuItemRepository getItems() {
        MenuItemRepository repository = new MenuItemRepository();
        List<ItemStack> stacks = (List<ItemStack>) getData("items");

        for (int i = 0; i < stacks.size(); i++) {
            repository.add(new MenuItem(stacks.get(i), i) {
                @Override
                public void onClick(Player player) { }
            });
        }

        return repository;
    }

}
