package net.llamasoftware.spigot.floatingpets.command;

import net.llamasoftware.spigot.floatingpets.Constants;
import net.llamasoftware.spigot.floatingpets.FloatingPets;
import net.llamasoftware.spigot.floatingpets.api.model.Pet;
import net.llamasoftware.spigot.floatingpets.locale.Locale;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedList;

public abstract class Command {

    public FloatingPets plugin;
    public Locale locale;

    public Pet pet;
    public int index;

    public Command(FloatingPets plugin){
        this.plugin = plugin;
        this.locale = plugin.getLocale();
    }

    public void handleCommand(CommandSender sender, String[] arguments){
        if (sender instanceof Player && getDeclaration().petContext()) {
            Player player = (Player) sender;
            LinkedList<Pet> pets = plugin.getStorageManager()
                    .getPetsByOwner(player.getUniqueId());

            if (pets.isEmpty()) {
                locale.send(player, "generic.no-pet", false);
                return;
            }

            String command = getDeclaration().name();

            Integer[] petId = plugin.getUtility().parsePetId(player, command, arguments,
                    getDeclaration().activePets(), false);

            if (petId == null)
                return;

            if(index >= pets.size() || index < 0)
                return;

            if(index == 0 && petId.length != 2){
                String ind = Constants.PET_COMMAND_NAME + " " + command + " " + index +
                        (arguments.length > 0 ? " " + String.join(" ", arguments) : "");

                Bukkit.dispatchCommand(player, ind);
                return;
            }

            pet = pets.get(index);
        }

        onCommand(sender, arguments);
    }

    public abstract void onCommand(CommandSender sender, String[] arguments);

    public CommandInfo getDeclaration(){
        return getClass().getAnnotation(CommandInfo.class);
    }

}
