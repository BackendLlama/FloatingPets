package gq.zunarmc.spigot.floatingpets.manager.storage.impl;

import gq.zunarmc.spigot.floatingpets.FloatingPets;
import gq.zunarmc.spigot.floatingpets.api.model.Skill;
import gq.zunarmc.spigot.floatingpets.manager.config.YAMLManager;
import gq.zunarmc.spigot.floatingpets.manager.storage.StorageManager;
import gq.zunarmc.spigot.floatingpets.api.model.Pet;
import gq.zunarmc.spigot.floatingpets.api.model.PetType;
import gq.zunarmc.spigot.floatingpets.api.model.Setting;
import gq.zunarmc.spigot.floatingpets.model.config.YAMLFile;
import gq.zunarmc.spigot.floatingpets.model.misc.Food;
import gq.zunarmc.spigot.floatingpets.model.pet.IParticle;
import gq.zunarmc.spigot.floatingpets.model.pet.IPet;
import gq.zunarmc.spigot.floatingpets.util.Utility;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class FlatfileStorageManager extends StorageManager {

    private final FloatingPets plugin;
    private final YAMLFile petDataFile,
                           petTypeFile,
                           miscFile,
                           localeFile;

    public FlatfileStorageManager(FloatingPets plugin){
        YAMLManager yamlManager = plugin.getYamlManager();

        this.plugin = plugin;
        plugin.getLogger().info("Loading storage files");
        this.petDataFile = yamlManager.loadIfNotExists(plugin.getStringSetting(Setting.GENERAL_STORAGE_FLATFILE_PETS));
        this.petTypeFile = yamlManager.loadIfNotExists(plugin.getStringSetting(Setting.GENERAL_STORAGE_FLATFILE_TYPE));
        this.miscFile    = yamlManager.loadIfNotExists(plugin.getStringSetting(Setting.GENERAL_STORAGE_FLATFILE_MISC));
        this.localeFile  = plugin.getDefaultLocaleFile();
    }

    @Override
    public void preload(Type storageType) {

        plugin.getLogger().info("Preloading " + storageType.name().toLowerCase());

        switch (storageType){
            case PET:{
                if(!petDataFile.isLoaded())
                    petDataFile.load();

                YamlConfiguration dataStorage = petDataFile.getConfiguration();
                ConfigurationSection petStorageSection = dataStorage.getConfigurationSection("pets");

                if(petStorageSection == null) {
                    plugin.getLogger().warning("Configuration section 'pets' does not exist in " + petDataFile.getFile().getName());
                    return;
                }

                loadStoredPets(petStorageSection);
                break;
            }

            case TYPE:{
                if(!petTypeFile.isLoaded())
                    petTypeFile.load();

                YamlConfiguration typeStorage = petTypeFile.getConfiguration();
                ConfigurationSection typeSection = typeStorage.getConfigurationSection("types");

                if(typeSection == null) {
                    plugin.getLogger().info("Configuration section 'types' does not exist in "
                            + petTypeFile.getFile().getName());
                    return;
                }

                loadStoredTypes(typeSection);
                break;
            }

            case MISC:{
                if(!miscFile.isLoaded())
                    miscFile.load();

                YamlConfiguration miscConfig = miscFile.getConfiguration();
                if(miscConfig == null || !miscConfig.contains("food_items")){
                    plugin.getLogger().warning("Misc configuration is incomplete");
                    return;
                }

                loadStoredFoodItems(miscConfig.getStringList("food_items"));
                break;
            }

            case LOCALE:{
                ConfigurationSection section = localeFile.getConfiguration().getConfigurationSection("locale");
                if(section == null)
                    return;

                section.getKeys(true)
                        .forEach(key -> cachedLocaleData.put(key, section.get(key)));

                break;
            }

        }
    }

    private void loadStoredPets(ConfigurationSection section){
        for(String uuidString : section.getKeys(false)){
            ConfigurationSection petSection = section.getConfigurationSection(uuidString);
            if(petSection == null)
                continue;

            UUID uniqueId = UUID.fromString(uuidString);
            UUID owner = UUID.fromString(Objects.requireNonNull(petSection.getString("owner")));
            Optional<PetType> type = getTypeByUniqueId(UUID.fromString(Objects.requireNonNull(petSection.getString("type"))));

            if(!type.isPresent()){
                plugin.getLogger().info("Pet type specified by pet '" + uniqueId.toString() + "' is unavailable.");
                return;
            }

            String name = petSection.getString("name");

            List<Skill> skills = petSection.getStringList("skills")
                    .stream()
                    .map(Utility::deserializeSkill)
                    .collect(Collectors.toList());

            IPet.IPetBuilder petBuilder = IPet.builder()
                                                .uniqueId(uniqueId)
                                                .owner(owner)
                                                .type(type.get())
                                                .skills(skills)
                                                .name(name);

            gq.zunarmc.spigot.floatingpets.api.model.Particle petParticle = null;
            if(petSection.contains("particle")){
                Particle particle = Particle.valueOf(petSection.getString("particle.type"));
                int speed = petSection.getInt("particle.speed");

                petParticle = new IParticle(particle, speed, plugin);
                petBuilder.particle(petParticle);
            }

            Pet pet = petBuilder.build();
            storePet(pet, false);

            if(petParticle != null)
                petParticle.setPet(pet);

        }
    }

    private void loadStoredTypes(ConfigurationSection section){
        for(String uniqueId : section.getKeys(false)){
            String name = section.getString(uniqueId + ".name");
            String texture = section.getString(uniqueId + ".texture");
            double price = 0;

            if(section.contains(uniqueId + ".price"))
                price = section.getDouble(uniqueId + ".price");

            cachedTypes.add(new PetType(UUID.fromString(uniqueId), name, texture, price));
        }

        plugin.getLogger().info("  Successfully loaded " + cachedTypes.size() + " pet type(s)");
    }

    private void loadStoredFoodItems(List<String> serializedFoodItems){
        for(String serializedFoodItem : serializedFoodItems){
            String[] data = serializedFoodItem.split(":");
            Material material = Material.valueOf(data[0]);
            int amount = Integer.parseInt(data[1]);
            double value = Double.parseDouble(data[2]);

            cachedFoodItems.add(new Food(material, amount, value));
        }

        plugin.getLogger().info("  Successfully loaded " + cachedFoodItems.size() + " food item(s)");
    }

    @Override
    public void storePet(Pet pet, boolean save) {

        cachedPets.add(pet);

        if(save){
            if(petDataFile.isLoaded())
                petDataFile.load();

            YamlConfiguration dataStorage = petDataFile.getConfiguration();
            ConfigurationSection petStorageSection = dataStorage.createSection("pets." + pet.getUniqueId());
            petStorageSection.set("owner", pet.getOwner().toString());
            petStorageSection.set("type", pet.getType().getUniqueId().toString());
            petStorageSection.set("name", pet.getName());

            if(pet.hasParticle()){
                petStorageSection.set("particle.type", pet.getParticle().getParticle().name());
                petStorageSection.set("particle.speed", pet.getParticle().getSpeed());
            }

            petDataFile.save();
        }

    }

    @Override
    public void updatePet(Pet pet, StorageManager.Action action) {
        switch (action){
            case REMOVE:{
                cachedPets.remove(pet);

                petDataFile.getConfiguration().set("pets." + pet.getUniqueId().toString(), null);
                petDataFile.save();
                break;
            }

            case RENAME:{
                updateValue(pet, "name", pet.getName());
                break;
            }

            case PARTICLE:{
                if(!pet.hasParticle()){
                    updateValue(pet, "particle", null);
                    return;
                }

                updateValue(pet, "particle.type", pet.getParticle().getParticle().name());
                updateValue(pet, "particle.speed", pet.getParticle().getSpeed());
                break;
            }

            default: {}
        }
    }

    private void updateValue(Pet pet, String key, Object value){
        if(!petDataFile.isLoaded())
            petDataFile.load();

        petDataFile.getConfiguration().set("pets." + pet.getUniqueId().toString() + "." + key, value);
        petDataFile.save();
    }

}