package net.llamasoftware.spigot.floatingpets.model.skill;

import lombok.Getter;
import net.llamasoftware.spigot.floatingpets.api.model.Pet;
import net.llamasoftware.spigot.floatingpets.api.model.Skill;

public class StorageSkill extends Skill {

    @Getter
    private int rows;

    public StorageSkill(Type type, int level) {
        super(type, level);
    }

    @Override
    public void parse(Object object) {
        rows = (int) object;
    }

    @Override
    public void applySkill(Pet pet) {}

}