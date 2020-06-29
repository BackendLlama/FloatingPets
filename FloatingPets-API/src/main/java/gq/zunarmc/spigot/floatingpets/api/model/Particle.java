package gq.zunarmc.spigot.floatingpets.api.model;

public interface Particle {

    void setPet(Pet pet);

    org.bukkit.Particle getParticle();

    int getSpeed();

    void start();

    void stop();

}