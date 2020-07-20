package gq.zunarmc.spigot.floatingpets.manager.config;

import gq.zunarmc.spigot.floatingpets.FloatingPets;
import gq.zunarmc.spigot.floatingpets.api.model.Setting;
import gq.zunarmc.spigot.floatingpets.api.model.Skill;
import gq.zunarmc.spigot.floatingpets.model.misc.ParticleInfo;
import gq.zunarmc.spigot.floatingpets.model.misc.SkillCategory;
import gq.zunarmc.spigot.floatingpets.model.misc.SkillLevel;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SettingManager {

    private final FloatingPets plugin;
    private final FileConfiguration config;

    @Getter
    private final List<ParticleInfo> enabledParticles;
    @Getter
    private final List<SkillCategory> skillCategories;

    public SettingManager(FloatingPets plugin) {
        this.plugin           = plugin;
        this.config           = plugin.getConfig();
        this.enabledParticles = loadEnabledParticles();
        this.skillCategories  = loadSkillCategories();
    }

    private List<ParticleInfo> loadEnabledParticles(){

        ConfigurationSection section = config.getConfigurationSection("settings.pet.particle.display");
        if(section == null)
            return new ArrayList<>();

        String filter = plugin.getStringSetting(Setting.PET_PARTICLE_FILTER);
        List<org.bukkit.Particle> particles;

        if(!filter.equalsIgnoreCase("none")){
            if(!(filter.equalsIgnoreCase("exclude") || filter.equalsIgnoreCase("include"))){
                plugin.getLogger().warning("Invalid particle filter specified in config.");
            }

            if(filter.equalsIgnoreCase("exclude")){
                List<String> excludedParticles = config.getStringList("settings.pet.particle.filter.exclude");
                particles = Arrays.stream(org.bukkit.Particle.values()).filter(particle ->
                        !excludedParticles.contains(particle.name())).collect(Collectors.toList());
            } else {
                List<String> includedParticles = config.getStringList("settings.pet.particle.filter.include");
                particles = Arrays.stream(org.bukkit.Particle.values()).filter(particle ->
                        includedParticles.contains(particle.name())).collect(Collectors.toList());
            }

        } else {
            particles = Arrays.stream(org.bukkit.Particle.values()).collect(Collectors.toList());
        }

        List<ParticleInfo> particleInfos = new ArrayList<>();

        ConfigurationSection displaySection = config.getConfigurationSection("settings.pet.particle.display");
        if(displaySection == null)
            return new ArrayList<>();

        for(org.bukkit.Particle part : particles) {
            String materialStr = displaySection.getString(part.name().toLowerCase());
            particleInfos.add(ParticleInfo.builder().particle(part).material(materialStr == null ?
                    Material.REDSTONE : Material.getMaterial(materialStr)).build());
        }

        return particleInfos;
    }

    public List<SkillCategory> loadSkillCategories(){

        ConfigurationSection section   = config.getConfigurationSection("settings.pet.skills.types");
        List<SkillCategory> categories = new ArrayList<>();

        if(section == null)
            return categories;

        for (String key : section.getKeys(false)) {

            ConfigurationSection skillSection = section.getConfigurationSection(key);
            if(skillSection == null)
                continue;

            Skill.Type type       = Skill.Type.valueOf(key);
            String displayItemStr = skillSection.getString("display");

            if(displayItemStr == null)
                continue;

            Material material = Material.getMaterial(displayItemStr);
            if(material == null)
                continue;

            ConfigurationSection levelSection = skillSection.getConfigurationSection("levels");
            if(levelSection == null)
                continue;

            List<SkillLevel> levels = new ArrayList<>();
            for (String levelSectionKey : levelSection.getKeys(false)) {
                int i = Integer.parseInt(levelSectionKey);
                Object value = levelSection.get(levelSectionKey + ".value");
                double cost  = levelSection.getDouble(levelSectionKey + ".cost");
                levels.add(SkillLevel
                            .builder()
                            .type(type)
                            .level(i)
                            .value(value)
                            .cost(cost)
                            .build());
            }

            categories.add(SkillCategory.builder()
                    .type(type)
                    .displayItem(material)
                    .levels(levels)
                    .build());

        }

        return categories;

    }

}