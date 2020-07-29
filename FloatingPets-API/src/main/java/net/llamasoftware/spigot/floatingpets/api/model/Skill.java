package net.llamasoftware.spigot.floatingpets.api.model;

import lombok.Getter;

public abstract class Skill {

    @Getter
    public final Type type;
    @Getter
    public final int level;

    public Skill(Type type, int level){
        this.type  = type;
        this.level = level;
    }

    public abstract void parse(Object object);

    public abstract void applySkill(Pet pet);

    public enum Implementation {

        ATTRIBUTE,
        BEACON,
        CUSTOM

    }

    public enum Type {
        MAX_HEALTH(Implementation.ATTRIBUTE),
        ATTACK_DAMAGE(Implementation.ATTRIBUTE),
        ATTACK_SPEED(Implementation.ATTRIBUTE),
        BEACON(Implementation.BEACON),
        STORAGE(Implementation.CUSTOM);

        @Getter
        private final Implementation implementation;

        Type(Implementation implementation) {
            this.implementation = implementation;
        }

    }

}