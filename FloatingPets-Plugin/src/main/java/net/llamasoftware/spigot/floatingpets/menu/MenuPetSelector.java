package net.llamasoftware.spigot.floatingpets.menu;

import net.llamasoftware.spigot.floatingpets.api.model.PetType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MenuPetSelector extends ListMenu<PetType> {

    public MenuPetSelector(String title, List<PetType> availableTypes) {
        super(title, 5);
        setData("menuIndex", 0);
        setData("list", availableTypes);
    }

    @Override
    public ItemStack buildItem(PetType type) {
        return getPlugin().getUtility().getPetDisplayItem(type).build();
    }

    @Override
    public void onClick(Player player, PetType type, int index) {
        player.closeInventory();
        Bukkit.getServer().dispatchCommand(player, "pet select " + type.getName());
    }

}