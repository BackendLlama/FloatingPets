package gq.zunarmc.spigot.floatingpets.command.subcommand;

import gq.zunarmc.spigot.floatingpets.FloatingPets;
import gq.zunarmc.spigot.floatingpets.command.Command;
import gq.zunarmc.spigot.floatingpets.command.CommandInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "calloff", aliases = {"cancel", "untarget"}, inGame = true)
public class CommandCalloff extends Command {

    public CommandCalloff(FloatingPets plugin) {
        super(plugin);
    }

    @Override
    public void onCommand(CommandSender sender, String[] arguments) {

        Player player = (Player) sender;

        pet.getEntity().removeTarget();
        locale.send(player, "commands.calloff.called-off", true);
    }

}