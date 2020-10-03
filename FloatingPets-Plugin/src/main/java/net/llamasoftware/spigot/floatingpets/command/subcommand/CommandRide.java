package net.llamasoftware.spigot.floatingpets.command.subcommand;

import net.llamasoftware.spigot.floatingpets.FloatingPets;
import net.llamasoftware.spigot.floatingpets.command.Command;
import net.llamasoftware.spigot.floatingpets.command.CommandInfo;
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

        if(pet.getEntity().getEntity().getPassengers().contains(player)){
            locale.send(player, "commands.ride.already-riding", false);
            return;
        }

        pet.setStill(false);
        pet.getNameTag().addPassenger(player);
        locale.send(player, "commands.ride.riding", true);

    }

}