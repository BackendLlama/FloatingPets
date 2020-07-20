package gq.zunarmc.spigot.floatingpets.model.misc;

import gq.zunarmc.spigot.floatingpets.api.model.Skill;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.Material;

import java.util.List;

@Builder
public class SkillCategory {

    @Getter
    private final Skill.Type type;
    @Getter
    private final Material displayItem;
    @Getter
    private final List<SkillLevel> levels;

}