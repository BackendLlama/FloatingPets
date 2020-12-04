package net.llamasoftware.spigot.floatingpets.model.pet;

import net.llamasoftware.spigot.floatingpets.FloatingPets;
import net.llamasoftware.spigot.floatingpets.api.model.Particle;
import net.llamasoftware.spigot.floatingpets.api.model.Pet;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Objects;

public class IParticle implements Particle {

    @Setter
    private transient Pet pet;
    @Setter
    private transient FloatingPets plugin;
    @Setter
    private transient boolean stop;

    @Getter public org.bukkit.Particle particle;
    @Getter int speed;

    public IParticle(org.bukkit.Particle particle, int speed, FloatingPets plugin) {
        this.particle = particle;
        this.speed    = speed;
        this.plugin   = plugin;
        this.stop = false;
    }

    @Override
    public void start(){
        Bukkit.getScheduler().runTaskTimer(plugin, bukkitTask -> {
            if(!plugin.getPetManager().isPetSpawned(pet) || !pet.hasParticle()
                    || !pet.isAlive() || pet.getOnlineOwner() == null) {
                return;
            }

            if(stop){
                bukkitTask.cancel();
                return;
            }

            Bukkit.getOnlinePlayers().stream()
                    .filter(Objects::nonNull)
                    .filter(player -> player.canSee(pet.getOnlineOwner()))
                    .forEach(this::spawnParticle);
        }, 0, speed);
    }

    private void spawnParticle(Player player){
        Location location = pet.getNameTag().getLocation().add(0,0.5,0);
        int i = 1;
        int v = 0;
        int v3 = 0;

        if(particle != org.bukkit.Particle.REDSTONE){
            player.spawnParticle(particle, location, i, v, v, v, v3);
        } else {
            player.spawnParticle(particle, location, i, v, v, v, v3,
                    new org.bukkit.Particle.DustOptions(Color.RED, 1));
        }
    }

    @Override
    public void stop(){
        stop = true;
    }

}