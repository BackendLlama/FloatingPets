package net.llamasoftware.spigot.floatingpets.model.misc;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Particle;

@Builder
public class ParticleInfo {

    @Getter
    private final Material material;
    @Getter
    private final Particle particle;

}