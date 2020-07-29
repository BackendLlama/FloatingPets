package net.llamasoftware.spigot.floatingpets.command.subcommand;

import net.llamasoftware.spigot.floatingpets.FloatingPets;
import net.llamasoftware.spigot.floatingpets.api.model.Setting;
import net.llamasoftware.spigot.floatingpets.command.Command;
import net.llamasoftware.spigot.floatingpets.command.CommandInfo;
import net.llamasoftware.spigot.floatingpets.task.PetLightTask;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "light", inGame = true)
public class CommandLight extends Command {

    public CommandLight(FloatingPets plugin) {
        super(plugin);
    }

    @Override
    public void onCommand(CommandSender sender, String[] arguments) {

        Player player = (Player) sender;

        if(!plugin.isSetting(Setting.PET_LIGHT_COSMETIC) ||
                Bukkit.getPluginManager().getPlugin("LightAPI") == null){

            locale.send(player, "generic.functionality-disabled", false);
            return;
        }

        if(pet.isLight()){
            pet.setLight(false);
            locale.send(player, "commands.light.detached", true);
            return;
        }

        pet.setLight(true);
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new PetLightTask(pet), 0, 5);
        locale.send(player, "commands.light.attached", true);

    }

}
