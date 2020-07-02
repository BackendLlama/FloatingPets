package gq.zunarmc.spigot.floatingpets.api.model;

public abstract class Skill {

    public final Type type;
    public final int level;

    public Skill(Type type, int level){
        this.type  = type;
        this.level = level;
    }

    public abstract void parse(Object object);

    public abstract void applySkill(Pet pet);

    public enum Type {
        MAX_HEALTH,
        ATTACK_DAMAGE,
        ATTACK_SPEED,
        BEACON,
        STORAGE
    }

}