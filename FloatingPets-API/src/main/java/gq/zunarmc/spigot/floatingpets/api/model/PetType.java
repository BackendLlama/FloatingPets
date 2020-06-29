package gq.zunarmc.spigot.floatingpets.api.model;

import lombok.Getter;

import java.util.UUID;

public class PetType {

    @Getter
    private final UUID uniqueId;
    @Getter
    private final String name;
    @Getter
    private final String texture;
    @Getter
    private final double price;

    public PetType(UUID uniqueId, String name, String texture, double price) {
        this.uniqueId = uniqueId;
        this.name     = name;
        this.texture  = texture;
        this.price    = price;
    }

    public String getPermission(){
        return "floatingpets.type." + getName().toLowerCase();
    }

}