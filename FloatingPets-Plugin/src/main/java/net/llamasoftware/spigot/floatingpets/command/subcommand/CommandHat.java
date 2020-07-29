package net.llamasoftware.spigot.floatingpets.command.subcommand;

import net.llamasoftware.spigot.floatingpets.FloatingPets;
import net.llamasoftware.spigot.floatingpets.api.model.Pet;
import net.llamasoftware.spigot.floatingpets.api.model.Setting;
import net.llamasoftware.spigot.floatingpets.command.Command;
import net.llamasoftware.spigot.floatingpets.command.CommandInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "hat", inGame = true)
public class CommandHat extends Command {

    public CommandHat(FloatingPets plugin) {
        super(plugin);
    }

    @Override
    public void onCommand(CommandSender sender, String[] arguments) {
        Player player = (Player) sender;

        if(!plugin.isSetting(Setting.PET_HAT_COSMETIC)){
            locale.send(player, "generic.functionality-disabled", false);
            return;
        }

        boolean toggle = toggleHat(pet, player);
        locale.send(player, "commands.hat." + (toggle ? "put" : "no-longer"), true);
    }

    private boolean toggleHat(Pet pet, Player player){
        boolean val = pet.isRiding(player);
        if(!val){
            pet.ride(player);
        } else {
            pet.stopRiding(player);
        }
        return !val;
    }

}
