package gq.zunarmc.spigot.floatingpets.command.subcommand;

import gq.zunarmc.spigot.floatingpets.FloatingPets;
import gq.zunarmc.spigot.floatingpets.command.Command;
import gq.zunarmc.spigot.floatingpets.command.CommandInfo;
import gq.zunarmc.spigot.floatingpets.locale.Locale;
import org.bukkit.command.CommandSender;

import java.util.List;

@CommandInfo(name = "help", petContext = false)
public class CommandHelp extends Command {

    public CommandHelp(FloatingPets plugin) {
        super(plugin);
    }

    @Override
    public void onCommand(CommandSender sender, String[] arguments) {

        List<Command> commands = plugin.getCommandManager().getCommands();

        plugin.getLocale().send(sender, "commands.help.index.delimiter", false);
        plugin.getLocale().send(sender, "", false);

        commands.stream().filter(command -> command.getDeclaration().list()).forEach(command -> {
            CommandInfo info = command.getDeclaration();
            String[] localizedInfo = plugin.getCommandManager().getLocalizedInfo(info.name());
            plugin.getLocale().send(sender, "commands.help.index.command", false,
                    new Locale.Placeholder("name", info.name()),
                    new Locale.Placeholder("description", localizedInfo[0]),
                    new Locale.Placeholder("syntax", localizedInfo[1].isEmpty() ? localizedInfo[1] : " " + localizedInfo[1]));
        });

        plugin.getLocale().send(sender, "", false);
        plugin.getLocale().send(sender, "commands.help.index.delimiter", false);

    }

}