package net.llamasoftware.spigot.floatingpets.command.subcommand;

import net.llamasoftware.spigot.floatingpets.FloatingPets;
import net.llamasoftware.spigot.floatingpets.command.Command;
import net.llamasoftware.spigot.floatingpets.command.CommandInfo;
import net.llamasoftware.spigot.floatingpets.locale.Locale;
import org.bukkit.command.CommandSender;

@CommandInfo(name = "removeall", list = false)
public class CommandRemoveAll extends Command {

    public CommandRemoveAll(FloatingPets plugin) {
        super(plugin);
    }

    @Override
    public void onCommand(CommandSender sender, String[] arguments) {
        Locale locale = plugin.getLocale();

        plugin.getPetManager().despawnPets();
        plugin.getNmsHelper().getNmsManager().killPets();

        locale.send(sender, "commands.removeall.removed", true);
    }

}