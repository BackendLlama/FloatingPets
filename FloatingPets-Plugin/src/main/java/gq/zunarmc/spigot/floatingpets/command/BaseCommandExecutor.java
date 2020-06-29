package gq.zunarmc.spigot.floatingpets.command;

import gq.zunarmc.spigot.floatingpets.FloatingPets;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;

public class BaseCommandExecutor implements CommandExecutor {

    private final FloatingPets plugin;
    public BaseCommandExecutor(FloatingPets plugin){
        this.plugin = plugin;
    }

    @Override @SuppressWarnings("NullableProblems")
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] arguments) {

        if(arguments.length == 0){
            plugin.getServer().dispatchCommand(sender, "floatingpets:pet help");
            return true;
        }

        Optional<Command> command = plugin.getCommandManager().getCommandByName(arguments[0]);
        if(!command.isPresent()){
            return true;
        }

        if(!sender.hasPermission("floatingpets.commands." + command.get().getDeclaration().name().toLowerCase())){
            plugin.getLocale().send(sender, "generic.no-permission", true);
            return true;
        }

        if(!(sender instanceof Player) && command.get().getDeclaration().inGame()){
            plugin.getLocale().send(sender, "generic.player-only", false);
            return true;
        }

        command.get().handleCommand(sender, arguments.length != 1 ?
                Arrays.copyOfRange(arguments, 1, arguments.length) : new String[0]);

        return true;

    }

}
