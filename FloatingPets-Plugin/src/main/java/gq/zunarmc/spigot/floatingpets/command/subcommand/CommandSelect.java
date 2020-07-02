package gq.zunarmc.spigot.floatingpets.command.subcommand;

import gq.zunarmc.spigot.floatingpets.FloatingPets;
import gq.zunarmc.spigot.floatingpets.command.Command;
import gq.zunarmc.spigot.floatingpets.command.CommandInfo;
import gq.zunarmc.spigot.floatingpets.locale.Locale;
import gq.zunarmc.spigot.floatingpets.manager.storage.StorageManager;
import gq.zunarmc.spigot.floatingpets.menu.MenuPetSelector;
import gq.zunarmc.spigot.floatingpets.menu.MenuPurchaseConfirmation;
import gq.zunarmc.spigot.floatingpets.api.model.Pet;
import gq.zunarmc.spigot.floatingpets.api.model.PetType;
import gq.zunarmc.spigot.floatingpets.api.model.Setting;
import gq.zunarmc.spigot.floatingpets.model.misc.Cooldown;
import gq.zunarmc.spigot.floatingpets.model.pet.IPet;
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
            MenuPetSelector menu = new MenuPetSelector(plugin.getStorageManager().getLocaleByKey("menus.selector.title"),
                    plugin.getStorageManager().getCachedTypes()
                            .stream()
                            .filter(type -> player.hasPermission(type.getPermission()))
                            .collect(Collectors.toList()));
            
            plugin.getMenuManager().openMenu(player, menu, plugin);
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

            Pet pet = IPet.builder()
                    .uniqueId(UUID.randomUUID())
                    .name(plugin.getLocale().transformPlaceholders(plugin.getStringSetting(Setting.PET_NAME_DEFAULT_NAME),
                            new Locale.Placeholder("owner", player.getName())))
                    .owner(player.getUniqueId())
                    .type(type.get())
                    .build();

            plugin.getStorageManager().storePet(pet, true);
            plugin.getPetManager().spawnPet(pet, player.getLocation(), player, true);

            if(settingCooldown) {
                long expiry = System.currentTimeMillis()
                        + 1000 * plugin.getUtility().getPermissionBasedSetting(player, "pet.cooldown.select.limits",
                                                                                       "select_cooldown", 0);

                plugin.getCooldownManager().addCooldown(player.getUniqueId(), Cooldown.Type.SELECT, expiry);
            }

        }
    }

}