package net.llamasoftware.spigot.floatingpets.api.model;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Builder
public class PetCategory {

    @Getter
    private final String id;
    @Getter
    private final String name;
    @Getter
    private final ItemStack displayItem;

}