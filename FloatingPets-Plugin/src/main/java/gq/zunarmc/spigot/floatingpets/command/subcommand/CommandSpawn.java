package gq.zunarmc.spigot.floatingpets.command.subcommand;

import gq.zunarmc.spigot.floatingpets.FloatingPets;
import gq.zunarmc.spigot.floatingpets.command.Command;
import gq.zunarmc.spigot.floatingpets.command.CommandInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "spawn", aliases = {"show"}, activePets = false)
public class CommandSpawn extends Command {

    public CommandSpawn(FloatingPets plugin) {
        super(plugin);
    }

    @Override
    public void onCommand(CommandSender sender, String[] arguments) {
        Player player = (Player) sender;

        if(plugin.getPetManager().isPetSpawned(pet)){
            locale.send(player, "commands.spawn.already-spawned", false);
            return;
        }

        if(plugin.getConfigDefinition().isExcludedWorld(player.getWorld().getName())){
            locale.send(player, "generic.world-restricted", false);
            return;
        }

        plugin.getPetManager().spawnPet(pet, player.getLocation(), player, true);
    }

}