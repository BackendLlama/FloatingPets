package gq.zunarmc.spigot.floatingpets.model.skill;

import gq.zunarmc.spigot.floatingpets.api.model.Pet;
import gq.zunarmc.spigot.floatingpets.api.model.Skill;

public class CustomSkill extends Skill {

    public CustomSkill(Type type, int level) {
        super(type, level);
    }

    @Override
    public void parse(Object object) {

    }

    @Override
    public void applySkill(Pet pet) {

    }

}