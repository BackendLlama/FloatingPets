package net.llamasoftware.spigot.floatingpets.command.subcommand;

import net.llamasoftware.spigot.floatingpets.FloatingPets;
import net.llamasoftware.spigot.floatingpets.api.model.Setting;
import net.llamasoftware.spigot.floatingpets.command.Command;
import net.llamasoftware.spigot.floatingpets.command.CommandInfo;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "ride", inGame = true)
public class CommandRide extends Command {

    public CommandRide(FloatingPets plugin) {
        super(plugin);
    }

    @Override
    public void onCommand(CommandSender sender, String[] arguments) {

        Player player = (Player) sender;

        if(!plugin.isSetting(Setting.PET_RIDING)
                || Bukkit.getServer().getPluginManager().getPlugin("ProtocolLib") == null){
            locale.send(player, "generic.functionality-disabled", false);
            return;
        }

        if(pet.getEntity().getEntity().getPassengers().contains(player)){
            locale.send(player, "commands.ride.already-riding", false);
            return;
        }

        pet.getEntity().getEntity().addPassenger(player);
        locale.send(player, "commands.ride.riding", true);

    }

}