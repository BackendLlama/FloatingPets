package net.llamasoftware.spigot.floatingpets.command.subcommand;

import net.llamasoftware.spigot.floatingpets.FloatingPets;
import net.llamasoftware.spigot.floatingpets.command.Command;
import net.llamasoftware.spigot.floatingpets.command.CommandInfo;
import net.llamasoftware.spigot.floatingpets.locale.Locale;
import net.llamasoftware.spigot.floatingpets.manager.storage.StorageManager;
import net.llamasoftware.spigot.floatingpets.menu.MenuCategoryList;
import net.llamasoftware.spigot.floatingpets.menu.MenuPetSelector;
import net.llamasoftware.spigot.floatingpets.menu.MenuPurchaseConfirmation;
import net.llamasoftware.spigot.floatingpets.api.model.Pet;
import net.llamasoftware.spigot.floatingpets.api.model.PetType;
import net.llamasoftware.spigot.floatingpets.api.model.Setting;
import net.llamasoftware.spigot.floatingpets.model.misc.Cooldown;
import net.llamasoftware.spigot.floatingpets.model.pet.IPet;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@CommandInfo(name = "select", inGame = true, petContext = false)
public class CommandSelect extends Command {

    public CommandSelect(FloatingPets plugin) {
        super(plugin);
    }

    @Override
    public void onCommand(CommandSender sender, String[] arguments) {

        Player player = (Player) sender;
        List<Pet> currentPets = plugin.getStorageManager().getPetsByOwner(player.getUniqueId());

        boolean settingCooldown = plugin.isSetting(Setting.PET_COOLDOWN_SELECT);

        if(settingCooldown){
            Optional<Cooldown> cooldown = plugin.getCooldownManager()
                    .getCooldown(player.getUniqueId(), Cooldown.Type.SELECT);

            if(cooldown.isPresent()) {
                locale.send(player, "cooldown.timeout", false,
                        new Locale.Placeholder("time", String.valueOf(cooldown.get().getTimeLeft() / 1000)));

                return;
            }
        }

        if(!plugin.isSetting(Setting.MULTIPLE_PETS)
                && !plugin.getPetManager().getPetByOwner(player).isEmpty()) {

            Pet currentPet = currentPets.get(0);
            locale.send(player, "commands.select.removed-current", true);
            plugin.getPetManager().despawnPet(currentPet);
            plugin.getStorageManager().updatePet(currentPet, StorageManager.Action.REMOVE);
        }

        if(plugin.isSetting(Setting.MULTIPLE_PETS) && plugin.getStorageManager()
                .getPetsByOwner(player.getUniqueId()).size()
                            >= plugin.getUtility().getPermissionBasedSetting(player, "pet.multiple_pets.limits",
                                                                                     "limit", Long.MAX_VALUE)){

            locale.send(player, "commands.select.pet-limit", true);
            return;
        }

        if(plugin.getConfigDefinition().isExcludedWorld(player.getWorld().getName())){
            locale.send(player, "generic.world-restricted", false);
            return;
        }

        if(arguments.length == 0){

            if(plugin.getConfig().contains("settings.pet.categories.enabled") &&
                    !plugin.getConfig().getBoolean("settings.pet.categories.enabled")) {

                plugin.getMenuManager().openMenu(player, new MenuCategoryList(plugin.getStorageManager().getLocaleByKey("menus.category.title"),
                        plugin.getSettingManager().getCategories()), plugin);
            } else {
                plugin.getMenuManager().openPetSelector(player, plugin.getSettingManager().getCategoryById("default").orElse(null));
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

            Optional<Pet> current = plugin.getStorageManager().getPetsByOwner(player.getUniqueId())
                    .stream().filter(p -> p.getType() == type.get()).findAny();

            if(current.isPresent()){
                locale.send(player, "commands.select.removed-current", true);
                plugin.getPetManager().despawnPet(current.get());
                plugin.getStorageManager().updatePet(current.get(), StorageManager.Action.REMOVE);
                return;
            }

            if(plugin.isSetting(Setting.PET_SHOP_ENABLED) && type.get().getPrice() > 0){
                MenuPurchaseConfirmation menu = new MenuPurchaseConfirmation(
                        plugin.getLocale().color(plugin.getStorageManager().getLocaleByKey("menus.purchase-confirmation.title")), type.get());

                plugin.getMenuManager().openMenu(player, menu, plugin);
                return;
            }

            plugin.getStorageManager().selectPet(player, type.get());

            if(settingCooldown) {
                long expiry = System.currentTimeMillis()
                        + 1000 * plugin.getUtility().getPermissionBasedSetting(player, "pet.cooldown.select.limits",
                                                                                       "select_cooldown", 0);

                plugin.getCooldownManager().addCooldown(player.getUniqueId(), Cooldown.Type.SELECT, expiry);
            }

        }
    }

}