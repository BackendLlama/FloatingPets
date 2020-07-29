package net.llamasoftware.spigot.floatingpets.menu;

import net.llamasoftware.spigot.floatingpets.Constants;
import net.llamasoftware.spigot.floatingpets.FloatingPets;
import net.llamasoftware.spigot.floatingpets.api.model.Pet;
import net.llamasoftware.spigot.floatingpets.api.model.Setting;
import net.llamasoftware.spigot.floatingpets.locale.Locale;
import net.llamasoftware.spigot.floatingpets.menu.model.Menu;
import net.llamasoftware.spigot.floatingpets.menu.model.MenuItem;
import net.llamasoftware.spigot.floatingpets.menu.model.MenuItemRepository;
import net.llamasoftware.spigot.floatingpets.model.misc.SkillCategory;
import net.llamasoftware.spigot.floatingpets.util.ItemBuilder;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

import java.util.List;

public class MenuSkillCategoryList extends Menu {

    public MenuSkillCategoryList(String title,
                                 Pet pet,
                                 List<SkillCategory> categories) {

        super(title, 1);
        setData("pet", pet);
        setData("categories", categories);
    }

    @SuppressWarnings("unchecked") @Override
    public MenuItemRepository getItems() {

        List<SkillCategory> categories = (List<SkillCategory>) getData("categories");
        final FloatingPets plugin = getPlugin();
        Pet pet = getData("pet", Pet.class);
        MenuItemRepository repository = new MenuItemRepository();

        for (int i = 0; i < categories.size(); i++) {
            SkillCategory category = categories.get(i);
            repository.add(new MenuItem(new ItemBuilder(category.getDisplayItem())
                    .name(plugin.getLocale()
                            .getText("skills." + category.getType().name().toLowerCase())).build(), i) {

                @Override
                public void onClick(Player player) {
                    if(!pet.getSkillOfType(category.getType()).isPresent()){

                        Economy economy = plugin.getEconomy();
                        double cost = category.getLevels().get(0).getCost();

                        if(economy.getBalance(player) < cost){
                            plugin.getLocale().send(player, "shop.no_afford", false);
                            return;
                        }

                        economy.depositPlayer(player, cost);
                        plugin.getLocale().send(player, "skill.bought", true,
                                new Locale.Placeholder("price",
                                        Constants.DEFAULT_DECIMAL_FORMAT.format(cost)),
                                new Locale.Placeholder("currency_symbol",
                                        plugin.getStringSetting(Setting.PET_SHOP_FORMAT_CURRENCY)));

                    }
                }

            });
        }

        return null;
    }

}
