package net.llamasoftware.spigot.floatingpets.menu;

import net.llamasoftware.spigot.floatingpets.Constants;
import net.llamasoftware.spigot.floatingpets.FloatingPets;
import net.llamasoftware.spigot.floatingpets.api.model.PetType;
import net.llamasoftware.spigot.floatingpets.api.model.Setting;
import net.llamasoftware.spigot.floatingpets.locale.Locale;
import net.llamasoftware.spigot.floatingpets.menu.model.Menu;
import net.llamasoftware.spigot.floatingpets.menu.model.MenuItem;
import net.llamasoftware.spigot.floatingpets.menu.model.MenuItemRepository;
import net.llamasoftware.spigot.floatingpets.util.ItemBuilder;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MenuPurchaseConfirmation extends Menu {

    public MenuPurchaseConfirmation(String title, PetType type) {
        super(title, 1);
        setData("type", type);
    }

    @Override
    public MenuItemRepository getItems() {
        FloatingPets plugin = getPlugin();
        PetType type = getData("type", PetType.class);
        ItemBuilder petItemBuilder = plugin.getUtility().getPetDisplayItem(type);

        return new MenuItemRepository().add(new MenuItem(new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name(
                plugin.getLocale()
                        .color(plugin.getStorageManager()
                                .getLocaleByKey("menus.purchase-confirmation.item-confirm"))).build(),2) {

            @Override
            public void onClick(Player player) {

                Economy economy = plugin.getEconomy();
                if(economy == null)
                    return;

                if(economy.getBalance(player) < type.getPrice()){
                    player.closeInventory();
                    plugin.getLocale().send(player, "shop.no_afford", false);
                    return;
                }

                economy.withdrawPlayer(player, type.getPrice());

                plugin.getLocale().send(player, "shop.bought",
                        true, new Locale.Placeholder("type", type.getName()),
                        new Locale.Placeholder("price",
                                Constants.DEFAULT_DECIMAL_FORMAT.format(type.getPrice())),
                        new Locale.Placeholder("currency_symbol",
                                plugin.getStringSetting(Setting.PET_SHOP_FORMAT_CURRENCY)));

                plugin.getStorageManager().selectPet(player, type);
                player.closeInventory();

            }

        }).add(new MenuItem(petItemBuilder.build(), 4) {
            @Override public void onClick(Player player) { }
        }).add(new MenuItem(new ItemBuilder(Material.RED_STAINED_GLASS_PANE).name(
                plugin.getLocale().color(plugin.getStorageManager().getLocaleByKey("menus.purchase-confirmation.item-cancel"))).build(),6) {
            @Override
            public void onClick(Player player) {
                player.closeInventory();
            }
        });
    }

}
