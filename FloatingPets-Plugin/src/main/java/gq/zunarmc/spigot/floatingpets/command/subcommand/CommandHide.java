package gq.zunarmc.spigot.floatingpets.command.subcommand;

import gq.zunarmc.spigot.floatingpets.FloatingPets;
import gq.zunarmc.spigot.floatingpets.command.Command;
import gq.zunarmc.spigot.floatingpets.command.CommandInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "hide", inGame = true)
public class CommandHide extends Command {

    public CommandHide(FloatingPets plugin) {
        super(plugin);
    }

    @Override
    public void onCommand(CommandSender sender, String[] arguments) {
        Player player = (Player) sender;

        if(!plugin.getPetManager().isPetSpawned(pet)){
            locale.send(player, "commands.hide.not-visible", false);
            return;
        }

        plugin.getPetManager().despawnPet(pet);
        locale.send(player, "commands.hide.hidden", true);
    }

}