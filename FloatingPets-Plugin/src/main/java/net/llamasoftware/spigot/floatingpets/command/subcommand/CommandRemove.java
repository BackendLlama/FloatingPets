package net.llamasoftware.spigot.floatingpets.command.subcommand;

import net.llamasoftware.spigot.floatingpets.FloatingPets;
import net.llamasoftware.spigot.floatingpets.api.model.Pet;
import net.llamasoftware.spigot.floatingpets.command.Command;
import net.llamasoftware.spigot.floatingpets.command.CommandInfo;
import net.llamasoftware.spigot.floatingpets.manager.storage.StorageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "remove")
public class CommandRemove extends Command {

    public CommandRemove(FloatingPets plugin) {
        super(plugin);
    }

    @Override
    public void onCommand(CommandSender sender, String[] arguments) {

        if(!(sender instanceof Player)){
            locale.send(sender, "commands.remove.admin-syntax", true);
            return;
        }

        Player player = (Player) sender;

        removePet(pet);
        locale.send(player, "commands.remove.removed", true);

    }

    private void removePet(Pet pet){
        plugin.getPetManager().despawnPet(pet);
        plugin.getStorageManager().updatePet(pet, StorageManager.Action.REMOVE);
    }

}