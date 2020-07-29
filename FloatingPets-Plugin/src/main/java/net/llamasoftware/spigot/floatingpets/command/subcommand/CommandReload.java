package net.llamasoftware.spigot.floatingpets.command.subcommand;

import net.llamasoftware.spigot.floatingpets.FloatingPets;
import net.llamasoftware.spigot.floatingpets.command.Command;
import net.llamasoftware.spigot.floatingpets.command.CommandInfo;
import org.bukkit.command.CommandSender;

@CommandInfo(name = "reload", list = false)
public class CommandReload extends Command {

    public CommandReload(FloatingPets plugin) {
        super(plugin);
    }

    @Override
    public void onCommand(CommandSender sender, String[] arguments) {
        plugin.getStorageManager().load();
        locale.send(sender, "commands.reload.reloaded", true);
    }

}