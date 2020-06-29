package gq.zunarmc.spigot.floatingpets.command.subcommand;

import gq.zunarmc.spigot.floatingpets.FloatingPets;
import gq.zunarmc.spigot.floatingpets.command.Command;
import gq.zunarmc.spigot.floatingpets.command.CommandInfo;
import gq.zunarmc.spigot.floatingpets.locale.Locale;
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