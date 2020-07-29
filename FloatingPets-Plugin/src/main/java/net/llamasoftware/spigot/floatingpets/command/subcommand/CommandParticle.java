package net.llamasoftware.spigot.floatingpets.command.subcommand;

import net.llamasoftware.spigot.floatingpets.FloatingPets;
import net.llamasoftware.spigot.floatingpets.api.model.Particle;
import net.llamasoftware.spigot.floatingpets.command.Command;
import net.llamasoftware.spigot.floatingpets.command.CommandInfo;
import net.llamasoftware.spigot.floatingpets.locale.Locale;
import net.llamasoftware.spigot.floatingpets.manager.storage.StorageManager;
import net.llamasoftware.spigot.floatingpets.menu.MenuPetParticle;
import net.llamasoftware.spigot.floatingpets.model.misc.ParticleInfo;
import net.llamasoftware.spigot.floatingpets.model.pet.IParticle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CommandInfo(name = "particle", inGame = true)
public class CommandParticle extends Command {

    private final List<ParticleInfo> enabledParticles;

    public CommandParticle(FloatingPets plugin){
        super(plugin);
        enabledParticles = plugin.getSettingManager().getEnabledParticles();
    }

    @Override
    public void onCommand(CommandSender sender, String[] arguments) {

        Player player = (Player) sender;

        if(arguments.length < 3){
            String firstArgument = arguments.length > 1 ? arguments[1] : "";
            boolean stop = (firstArgument.equalsIgnoreCase("stop")
                            || firstArgument.equalsIgnoreCase("cancel")
                            || firstArgument.equalsIgnoreCase("off"));

            if(!stop) {
                List<ParticleInfo> list = enabledParticles
                        .stream()
                        .filter(info ->
                                player.hasPermission("floatingpets.particle." + info.getParticle().name().toLowerCase()))
                        .collect(Collectors.toList());

                MenuPetParticle menu = new MenuPetParticle(plugin.getStorageManager()
                        .getLocaleByKey("menus.particle.title"), list);

                menu.setData("pet", pet);
                menu.setData("index", index);
                plugin.getMenuManager().openMenu(player, menu, plugin);
            } else {
                if(!pet.hasParticle()){
                    locale.send(player, "commands.particle.no-particle", false);
                    return;
                }

                pet.getParticle().stop();
                pet.setParticle(null);
                plugin.getStorageManager().updatePet(pet, StorageManager.Action.PARTICLE);
                locale.send(player, "commands.particle.stopped", true);
                return;
            }

            return;
        }

        if(!isParticleExisting(arguments[1])){
            locale.send(player, "commands.particle.invalid-type", false);
            return;
        }

        org.bukkit.Particle particleType = org.bukkit.Particle.valueOf(arguments[1]);

        if(!player.hasPermission("floatingpets.particle." + particleType.name().toLowerCase())){
            locale.send(player, "commands.particle.no-permission", false);
            return;
        }

        int speed = Integer.parseInt(arguments[2]);

        Particle particle = new IParticle(particleType, speed, plugin);
        pet.setParticle(particle);
        particle.setPet(pet);

        plugin.getStorageManager().updatePet(pet, StorageManager.Action.PARTICLE);

        locale.send(player, "commands.particle.set",
                true, new Locale.Placeholder("name", particleType.name()),
                new Locale.Placeholder("speed", String.valueOf(speed)));
    }

    private boolean isParticleExisting(String argument){
        return Arrays.stream(org.bukkit.Particle.values())
                .anyMatch(particle -> particle.name().equalsIgnoreCase(argument));
    }

}