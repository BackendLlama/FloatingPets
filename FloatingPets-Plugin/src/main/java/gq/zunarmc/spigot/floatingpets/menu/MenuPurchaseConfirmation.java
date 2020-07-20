package gq.zunarmc.spigot.floatingpets.menu;

import gq.zunarmc.spigot.floatingpets.Constants;
import gq.zunarmc.spigot.floatingpets.FloatingPets;
import gq.zunarmc.spigot.floatingpets.api.model.Pet;
import gq.zunarmc.spigot.floatingpets.api.model.PetType;
import gq.zunarmc.spigot.floatingpets.api.model.Setting;
import gq.zunarmc.spigot.floatingpets.locale.Locale;
import gq.zunarmc.spigot.floatingpets.menu.model.Menu;
import gq.zunarmc.spigot.floatingpets.menu.model.MenuItem;
import gq.zunarmc.spigot.floatingpets.menu.model.MenuItemRepository;
import gq.zunarmc.spigot.floatingpets.model.pet.IPet;
import gq.zunarmc.spigot.floatingpets.util.ItemBuilder;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.UUID;

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

                Pet pet = IPet.builder()
                        .uniqueId(UUID.randomUUID())
                        .name(plugin.getLocale().transformPlaceholders(plugin.getStringSetting(Setting.PET_NAME_DEFAULT_NAME),
                                new Locale.Placeholder("owner", player.getName())))
                        .owner(player.getUniqueId())
                        .type(type)
                        .build();

                plugin.getLocale().send(player, "shop.bought",
                        true, new Locale.Placeholder("type", type.getName()),
                        new Locale.Placeholder("price",
                                Constants.DEFAULT_DECIMAL_FORMAT.format(type.getPrice())),
                        new Locale.Placeholder("currency_symbol",
                                plugin.getStringSetting(Setting.PET_SHOP_FORMAT_CURRENCY)));

                plugin.getStorageManager().storePet(pet, true);
                plugin.getPetManager().spawnPet(pet, player.getLocation(), player, true);
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
