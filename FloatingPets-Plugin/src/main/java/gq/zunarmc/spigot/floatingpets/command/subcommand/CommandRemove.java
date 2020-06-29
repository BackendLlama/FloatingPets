package gq.zunarmc.spigot.floatingpets.command.subcommand;

import gq.zunarmc.spigot.floatingpets.FloatingPets;
import gq.zunarmc.spigot.floatingpets.api.model.Pet;
import gq.zunarmc.spigot.floatingpets.command.Command;
import gq.zunarmc.spigot.floatingpets.command.CommandInfo;
import gq.zunarmc.spigot.floatingpets.manager.storage.StorageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "remove")
public class CommandRemove extends Command {

    public CommandRemove(FloatingPets plugin) {
        super(plugin);
    }

    @Override
    public void onCommand(CommandSender sender, String[] arguments) {

        // TODO remove pet from console

        if (!(sender instanceof Player)) {
            locale.send(sender, "generic.player-only", false);
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