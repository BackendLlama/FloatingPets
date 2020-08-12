package net.llamasoftware.spigot.floatingpets.menu;

import net.llamasoftware.spigot.floatingpets.FloatingPets;
import net.llamasoftware.spigot.floatingpets.api.model.Pet;
import net.llamasoftware.spigot.floatingpets.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MenuPetSpecification extends ListMenu<Pet> {

    public MenuPetSpecification(String title, List<Pet> pets) {
        super(title, 4);
        setData("list", pets);
        setData("menuIndex", 0);
    }

    @Override
    public ItemStack buildItem(Pet pet) {
        FloatingPets plugin = getPlugin();
        ItemStack stack = new ItemBuilder(plugin.getNmsHelper()
                .getItemStackFromTexture(pet.getType().getTexture()))
                .name(plugin.getUtility().formatTitle(pet, getData("coloredNames", Boolean.class))).build();

        return stack;
    }

    @Override
    public void onClick(Player player, Pet object, int index) {
        String command = getData("command", String.class);
        String[] args  = getData("args", String[].class);

        player.closeInventory();
        Bukkit.dispatchCommand(player, command + " " + index
                + (args != null && args.length > 0 ? " " + String.join(" ", args) : ""));
    }

}