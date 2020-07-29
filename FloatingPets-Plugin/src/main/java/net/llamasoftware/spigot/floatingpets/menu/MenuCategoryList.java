package net.llamasoftware.spigot.floatingpets.menu;

import net.llamasoftware.spigot.floatingpets.api.model.PetCategory;
import net.llamasoftware.spigot.floatingpets.util.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MenuCategoryList extends ListMenu<PetCategory> {

    public MenuCategoryList(String title,
                            List<PetCategory> categories) {

        super(title, 2);
        setData("menuIndex", 0);
        setData("list", categories);
    }

    @Override
    public ItemStack buildItem(PetCategory category) {
        return new ItemBuilder(category.getDisplayItem()).name("&3" + category.getName()).build();
    }

    @Override
    public void onClick(Player player, PetCategory category, int index) {
        getMenuManager().openPetSelector(player, category);
    }

}