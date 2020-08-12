package net.llamasoftware.spigot.floatingpets.model.misc;

import lombok.Builder;
import lombok.Getter;
import net.llamasoftware.spigot.floatingpets.api.model.Skill;
import org.bukkit.Material;

import java.util.LinkedList;

@Builder
public class SkillCategory {

    @Getter
    private final Skill.Type type;
    @Getter
    private final Material displayItem;
    @Getter
    private final LinkedList<SkillLevel> levels;

}