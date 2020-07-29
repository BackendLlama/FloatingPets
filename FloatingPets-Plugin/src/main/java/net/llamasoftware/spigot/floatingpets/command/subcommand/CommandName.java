package net.llamasoftware.spigot.floatingpets.command.subcommand;

import com.google.common.base.Joiner;
import net.llamasoftware.spigot.floatingpets.FloatingPets;
import net.llamasoftware.spigot.floatingpets.api.model.Setting;
import net.llamasoftware.spigot.floatingpets.command.Command;
import net.llamasoftware.spigot.floatingpets.command.CommandInfo;
import net.llamasoftware.spigot.floatingpets.locale.Locale;
import net.llamasoftware.spigot.floatingpets.manager.storage.StorageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

@CommandInfo(name = "name", aliases = {"rename", "setname"}, minimumArguments = 1, inGame = true)
public class CommandName extends Command {

    public CommandName(FloatingPets plugin) {
        super(plugin);
    }

    @Override
    public void onCommand(CommandSender sender, String[] arguments) {

        Locale locale = plugin.getLocale();
        Player player = (Player) sender;

        if(!plugin.isSetting(Setting.PET_NAME_CUSTOM)){
            locale.send(player, "generic.functionality-disabled", false);
            return;
        }

        if(arguments.length == 0){
            return;
        }

        String value = Joiner.on(" ")
                .join(Arrays.copyOfRange(arguments, 1, arguments.length));

        if(value.length() < Integer.parseInt(plugin.getStringSetting(Setting.PET_NAME_CUSTOM_MINIMUM_LENGTH))){
            locale.send(player, "commands.name.too-short",
                    false, new Locale.Placeholder("min_length",
                            plugin.getStringSetting(Setting.PET_NAME_CUSTOM_MINIMUM_LENGTH)));
            return;
        }

        if(value.length() > Integer.parseInt(plugin.getStringSetting(Setting.PET_NAME_CUSTOM_MAXIMUM_LENGTH))){
            locale.send(player, "commands.name.too-long",
                    false, new Locale.Placeholder("max_length",
                            plugin.getStringSetting(Setting.PET_NAME_CUSTOM_MAXIMUM_LENGTH)));
            return;
        }

        pet.setName(value);

        plugin.getStorageManager().updatePet(pet, StorageManager.Action.RENAME);
        locale.send(player, "commands.name.named", true, new Locale.Placeholder("name", pet.getName()));

    }

}