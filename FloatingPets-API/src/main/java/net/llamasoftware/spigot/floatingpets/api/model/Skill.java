package net.llamasoftware.spigot.floatingpets.api.model;

import lombok.Getter;
import lombok.Setter;

public abstract class Skill {

    @Getter
    public final Type type;
    @Getter @Setter
    public int level;

    public Skill(Type type, int level){
        this.type  = type;
        this.level = level;
    }

    public abstract void parse(Object object);

    public abstract void applySkill(Pet pet);

    public enum Implementation {

        ATTRIBUTE,
        BEACON,
        STORAGE,

    }

    public enum Type {
        MAX_HEALTH(Implementation.ATTRIBUTE),
        ATTACK_DAMAGE(Implementation.ATTRIBUTE),
        ATTACK_SPEED(Implementation.ATTRIBUTE),
        BEACON(Implementation.BEACON),
        STORAGE(Implementation.STORAGE);

        @Getter
        private final Implementation implementation;

        Type(Implementation implementation) {
            this.implementation = implementation;
        }

    }

}