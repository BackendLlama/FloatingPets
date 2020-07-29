package net.llamasoftware.spigot.floatingpets.model.misc;

import lombok.Getter;
import org.bukkit.Material;

public class Food {

    @Getter
    private final Material material;
    @Getter
    private final int amount;
    @Getter
    private final double value;

    public Food(Material material, int amount, double value) {
        this.material = material;
        this.amount = amount;
        this.value = value;
    }

}