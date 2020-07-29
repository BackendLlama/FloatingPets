package net.llamasoftware.spigot.floatingpets.api.model;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
public class PetType {

    @Getter
    private final UUID uniqueId;
    @Getter
    private final String name;
    @Getter
    private final String texture;
    @Getter
    private final PetCategory category;
    @Getter
    private final double price;

    public String getPermission(){
        return "floatingpets.type." + getName().toLowerCase();
    }

}