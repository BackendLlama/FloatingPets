package net.llamasoftware.spigot.floatingpets.command.subcommand;

import net.llamasoftware.spigot.floatingpets.FloatingPets;
import net.llamasoftware.spigot.floatingpets.api.model.PetType;
import net.llamasoftware.spigot.floatingpets.api.model.Setting;
import net.llamasoftware.spigot.floatingpets.command.Command;
import net.llamasoftware.spigot.floatingpets.command.CommandInfo;
import net.llamasoftware.spigot.floatingpets.menu.MenuCategoryList;
import net.llamasoftware.spigot.floatingpets.menu.MenuPurchaseConfirmation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

@CommandInfo(name = "select", inGame = true, petContext = false)
public class CommandSelect extends Command {

    public CommandSelect(FloatingPets plugin) {
        super(plugin);
    }

    @Override
    public void onCommand(CommandSender sender, String[] arguments) {

        Player player = (Player) sender;

        if(arguments.length == 0){

            if(plugin.getConfig().contains("settings.pet.categories.enabled") &&
                    plugin.getConfig().getBoolean("settings.pet.categories.enabled") &&
                    !(plugin.getSettingManager().getCategories().size() < 2)) {

                plugin.getMenuManager().openMenu(player, new MenuCategoryList(plugin.getStorageManager()
                        .getLocaleByKey("menus.category.title"),
                        plugin.getSettingManager().getCategories()), plugin);
            } else {
                plugin.getMenuManager().openPetSelector(player,
                        plugin.getSettingManager().getCategoryById("default").orElse(null));
            }

        } else {

            Optional<PetType> type = plugin.getStorageManager().getTypeByName(arguments[0]);
            if (!type.isPresent()) {
                locale.send(player, "commands.select.invalid-type", false);
                return;
            }

            if(!player.hasPermission(type.get().getPermission())){
                locale.send(player, "commands.select.no-permission", false);
                return;
            }

            if(plugin.isSetting(Setting.PET_SHOP_ENABLED) && type.get().getPrice() > 0){
                MenuPurchaseConfirmation menu = new MenuPurchaseConfirmation(
                        plugin.getLocale().color(plugin.getStorageManager().getLocaleByKey("menus.purchase-confirmation.title")), type.get());

                plugin.getMenuManager().openMenu(player, menu, plugin);
                return;
            }

            plugin.getStorageManager().selectPet(player, type.get());

        }

    }

}