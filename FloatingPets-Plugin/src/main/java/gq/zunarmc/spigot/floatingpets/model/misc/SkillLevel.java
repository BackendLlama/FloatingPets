package gq.zunarmc.spigot.floatingpets.model.misc;

import gq.zunarmc.spigot.floatingpets.api.model.Skill;
import gq.zunarmc.spigot.floatingpets.util.Utility;
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
        return Utility.getSkillFromType(type, level);
    }

}