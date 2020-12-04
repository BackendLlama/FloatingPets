package net.llamasoftware.spigot.floatingpets.command.subcommand;

import net.llamasoftware.spigot.floatingpets.FloatingPets;
import net.llamasoftware.spigot.floatingpets.api.model.Setting;
import net.llamasoftware.spigot.floatingpets.command.Command;
import net.llamasoftware.spigot.floatingpets.command.CommandInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "teleport", aliases = {"call", "tp"}, inGame = true)
public class CommandTeleport extends Command {

    public CommandTeleport(FloatingPets plugin) {
        super(plugin);
    }

    @Override
    public void onCommand(CommandSender sender, String[] arguments) {

        Player player = (Player) sender;

        if(!plugin.isSetting(Setting.PET_TELEPORTATION_CALL)){
            locale.send(player, "generic.funtionality-disabled", false);
            return;
        }

        pet.getEntity().removeTarget();
        pet.getEntity().teleportToOwner();
        locale.send(player, "commands.teleport.teleported", true);

    }

}