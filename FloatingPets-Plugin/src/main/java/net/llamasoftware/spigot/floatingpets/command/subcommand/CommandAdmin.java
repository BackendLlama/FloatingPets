package net.llamasoftware.spigot.floatingpets.command.subcommand;

import net.llamasoftware.spigot.floatingpets.FloatingPets;
import net.llamasoftware.spigot.floatingpets.api.model.Pet;
import net.llamasoftware.spigot.floatingpets.command.Command;
import net.llamasoftware.spigot.floatingpets.command.CommandInfo;
import net.llamasoftware.spigot.floatingpets.manager.storage.StorageManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Optional;
import java.util.UUID;

@CommandInfo(name = "admin")
public class CommandAdmin extends Command {

    public CommandAdmin(FloatingPets plugin) {
        super(plugin);
    }

    @Override
    public void onCommand(CommandSender sender, String[] arguments) {
        switch (arguments[0]){
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
                    locale.send(sender, "commands.admin.remove.invalid", true);
                    return;
                }

                plugin.getPetManager().despawnPet(pet.get());
                plugin.getStorageManager().updatePet(pet.get(), StorageManager.Action.REMOVE);

                locale.send(sender, "commands.admin.remove.removed", true);
                break;
            }
        }

    }

}