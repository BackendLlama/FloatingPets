package net.llamasoftware.spigot.floatingpets.model.misc;

import net.llamasoftware.spigot.floatingpets.api.model.Skill;
import net.llamasoftware.spigot.floatingpets.util.Utility;
import lombok.Builder;
import lombok.Getter;

@Builder
public class SkillLevel {

    @Getter
    private final Skill.Type type;
    @Getter
    private final int level;
    @Getter
    private final double cost;
    @Getter
    private final Object value;

    public Skill getSkill(){
        Skill skill = Utility.getSkillFromType(type, level);

        if(skill != null)
            skill.parse(value);

        return skill;
    }

}