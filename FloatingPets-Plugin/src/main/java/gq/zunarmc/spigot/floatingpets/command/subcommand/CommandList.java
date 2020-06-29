package gq.zunarmc.spigot.floatingpets.command.subcommand;

import gq.zunarmc.spigot.floatingpets.FloatingPets;
import gq.zunarmc.spigot.floatingpets.command.Command;
import gq.zunarmc.spigot.floatingpets.command.CommandInfo;
import gq.zunarmc.spigot.floatingpets.locale.Locale;
import gq.zunarmc.spigot.floatingpets.api.model.PetType;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

@CommandInfo(name = "list", petContext = false)
public class CommandList extends Command {

    public CommandList(FloatingPets plugin) {
        super(plugin);
    }

    @Override
    public void onCommand(CommandSender sender, String[] arguments) {

        List<PetType> types = plugin.getStorageManager().getCachedTypes().stream()
                .filter(type -> sender.hasPermission("floatingpets.type." + type.getName().toLowerCase()))
                .collect(Collectors.toList());

        locale.send(sender, "commands.list.header", false,
                new Locale.Placeholder("amount", String.valueOf(types.size())));

        types.forEach(type -> locale.send(sender, "commands.list.format", false,
                new Locale.Placeholder("name", type.getName()),
                new Locale.Placeholder("shopInfo", plugin.getUtility().formatPrice(type.getPrice()))));

    }

}