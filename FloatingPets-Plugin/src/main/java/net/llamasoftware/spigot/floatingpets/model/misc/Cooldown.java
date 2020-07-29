package net.llamasoftware.spigot.floatingpets.model.misc;

import lombok.Builder;
import lombok.Getter;

@Builder
public class Cooldown {

    @Getter
    private final Type type;
    private final long expiry;

    public long getTimeLeft(){
        return expiry - System.currentTimeMillis();
    }

    public boolean isExpired(){
        return System.currentTimeMillis() >= expiry;
    }


    public enum Type {

        SELECT

    }


}