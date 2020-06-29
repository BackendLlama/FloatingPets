package gq.zunarmc.spigot.floatingpets.manager.command;

import gq.zunarmc.spigot.floatingpets.FloatingPets;
import gq.zunarmc.spigot.floatingpets.command.Command;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandManager {

    @Getter
    private final List<Command> commands = new ArrayList<>();

    private final FloatingPets plugin;
    public CommandManager(FloatingPets plugin){
        this.plugin = plugin;
    }

    public void registerCommand(Command command){
        if(commandExistsByName(command.getDeclaration().name())){
            plugin.getLogger().warning("Unable to register command '" +
                    command.getDeclaration().name() + "' as it's already registered.");
            return;
        }

        commands.add(command);
    }

    private boolean commandExistsByName(String name){
        return getCommandByName(name).isPresent();
    }

    public Optional<Command> getCommandByName(String name) {
        return commands.stream()
                .filter(command -> command.getDeclaration().name().equalsIgnoreCase(name)
                        || Arrays.stream(command.getDeclaration().aliases())
                                         .collect(Collectors.toList()).contains(name.toLowerCase()))
                .findAny();
    }

    public String[] getLocalizedInfo(String name){
        String key = "commands.help.info." + name + ".";
        return new String[]{plugin.getStorageManager().getLocaleByKey(key + "description"),
                plugin.getStorageManager().getLocaleByKey(key + "syntax")};
    }

}