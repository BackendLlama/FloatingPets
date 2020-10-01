package net.llamasoftware.spigot.floatingpets.command.subcommand;

import net.llamasoftware.spigot.floatingpets.FloatingPets;
import net.llamasoftware.spigot.floatingpets.api.model.Pet;
import net.llamasoftware.spigot.floatingpets.api.model.PetCategory;
import net.llamasoftware.spigot.floatingpets.api.model.PetType;
import net.llamasoftware.spigot.floatingpets.command.Command;
import net.llamasoftware.spigot.floatingpets.command.CommandInfo;
import net.llamasoftware.spigot.floatingpets.locale.Locale;
import net.llamasoftware.spigot.floatingpets.manager.storage.StorageManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CommandInfo(name = "admin", list = false, petContext = false)
public class CommandAdmin extends Command {

    public CommandAdmin(FloatingPets plugin) {
        super(plugin);
    }

    @Override
    public void onCommand(CommandSender sender, String[] arguments) {

        if(arguments.length == 0){
            List<String> msgList = plugin.getStorageManager().getLocaleListByKey("commands.admin.help");
            for (String s : msgList) {
                sender.sendMessage(locale.color(s));
            }
            return;
        }

        switch (arguments[0]){
            case "type":{

                if(arguments.length == 1)
                    return;

                switch (arguments[1]){
                    case "create":{

                        if(arguments.length != 4){
                            plugin.getLocale().send(sender, "commands.admin.type.create.syntax", false);
                            return;
                        }

                        String name    = arguments[2];
                        String texture = arguments[3];
                        StorageManager storageManager = plugin.getStorageManager();

                        if(storageManager.getTypeByName(name).isPresent()){
                            plugin.getLocale().send(sender, "commands.admin.type.create.already_exists", false);
                            return;
                        }

                        PetCategory defaultCategory = plugin.getSettingManager().getCategoryById("default")
                                .orElse(null);

                        storageManager.storeType(
                                PetType.builder()
                                        .uniqueId(UUID.randomUUID())
                                        .name(name)
                                        .texture(texture)
                                        .category(defaultCategory)
                                        .build());

                        plugin.getLocale().send(sender, "commands.admin.type.create.created", true,
                                new Locale.Placeholder("name", name));

                        break;
                    }

                    case "remove":{
                        if(arguments.length != 3){
                            plugin.getLocale().send(sender, "commands.admin.type.remove.syntax", false);
                            return;
                        }

                        StorageManager storageManager = plugin.getStorageManager();
                        String name = arguments[2];

                        Optional<PetType> type = storageManager.getTypeByName(name);

                        if(!type.isPresent()){
                            plugin.getLocale().send(sender, "commands.admin.type.remove.no_exist", false);
                            return;
                        }

                        plugin.getLocale().send(sender, "commands.admin.type.remove.removed", true,
                                new Locale.Placeholder("name", name));

                        storageManager.removeType(type.get());
                        break;
                    }
                }

                break;
            }
            case "remove":{
                if(arguments.length != 3){
                    locale.send(sender, "commands.admin.remove.syntax", false);
                    return;
                }

                @SuppressWarnings("deprecation") // Deprecated API usage since method is used for it's intended purpose.
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(arguments[1]);
                UUID uniqueId = offlinePlayer.getUniqueId();

                Optional<Pet> pet = plugin.getStorageManager()
                        .getPetsByOwner(uniqueId)
                            .stream()
                            .filter(p -> p.getType().getName().equalsIgnoreCase(arguments[2]))
                            .findFirst();

                if(!pet.isPresent()) {
                    locale.send(sender, "commands.admin.remove.invalid", false);
                    return;
                }

                plugin.getPetManager().despawnPet(pet.get());
                plugin.getStorageManager().updatePet(pet.get(), StorageManager.Action.REMOVE);

                locale.send(sender, "commands.admin.remove.removed", true,
                        new Locale.Placeholder("player", offlinePlayer.getName()),
                        new Locale.Placeholder("type", pet.get().getType().getName()));
                break;
            }

            case "give":{

                if(arguments.length != 3){
                    locale.send(sender, "commands.admin.give.syntax", false);
                    return;
                }

                @SuppressWarnings("deprecation") // Deprecated API usage since method is used for it's intended purpose.
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(arguments[1]);

                Optional<PetType> type = plugin.getStorageManager().getTypeByName(arguments[2]);
                if (!type.isPresent()) {
                    locale.send(sender, "commands.select.invalid-type", false);
                    return;
                }

                if(offlinePlayer.isOnline()) {
                    Player player = (Player) offlinePlayer;
                    plugin.getStorageManager().selectPet(player, type.get());
                } else {
                    plugin.getStorageManager().createPet(type.get(), offlinePlayer);
                }

                locale.send(sender, "commands.admin.give.given", true,
                        new Locale.Placeholder("player", offlinePlayer.getName()),
                        new Locale.Placeholder("type", type.get().getName()));

                return;

            }

            default:
                break;
        }

    }

}