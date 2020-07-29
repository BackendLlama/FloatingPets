package net.llamasoftware.spigot.floatingpets.model.misc;

import net.llamasoftware.spigot.floatingpets.api.model.Skill;
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