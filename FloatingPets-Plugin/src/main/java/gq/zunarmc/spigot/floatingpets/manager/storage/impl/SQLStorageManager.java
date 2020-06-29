package gq.zunarmc.spigot.floatingpets.manager.storage.impl;

import gq.zunarmc.spigot.floatingpets.FloatingPets;
import gq.zunarmc.spigot.floatingpets.manager.sql.MySQLManager;
import gq.zunarmc.spigot.floatingpets.manager.storage.StorageManager;
import gq.zunarmc.spigot.floatingpets.api.model.Pet;
import gq.zunarmc.spigot.floatingpets.api.model.PetType;
import gq.zunarmc.spigot.floatingpets.api.model.Setting;
import gq.zunarmc.spigot.floatingpets.model.misc.Food;
import gq.zunarmc.spigot.floatingpets.model.pet.IParticle;
import gq.zunarmc.spigot.floatingpets.model.pet.IPet;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SQLStorageManager extends StorageManager {

    private final FloatingPets plugin;
    private final MySQLManager mySqlManager;
    private final String prefix;
    private final YamlConfiguration defaultLocale;

    public SQLStorageManager(FloatingPets plugin, MySQLManager mySqlManager){
        this.mySqlManager     = mySqlManager;
        this.plugin           = plugin;
        this.prefix           = plugin.getStringSetting(Setting.GENERAL_STORAGE_MYSQL_PREFIX);
        this.defaultLocale    = plugin.getDefaultLocaleFile().getConfiguration();
    }

    @Override
    public void preload(Type storageType) {

        createTables();

        String table = getTable(storageType.name().toLowerCase());
        CachedRowSet result = mySqlManager.query("SELECT * FROM " + table);

        if(result == null) {
            plugin.getLogger().warning("Unable to load " + storageType.name().toLowerCase() + " from MySQL source");
            return;
        }

        plugin.getLogger().info("Preloading " + storageType.name());

        switch (storageType){
            case LOCALE:

                try {
                    while (result.next()) {
                        String key   = result.getString("l_key");
                        String value = result.getString("value");

                        cachedLocaleData.put(key, value);
                    }
                } catch (SQLException ex){
                    plugin.getLogger().warning("An error occurred preloading " + storageType.name());
                    ex.printStackTrace();
                }

                List<LocaleItem> items = getDefaultLocaleValues();
                items.forEach(item -> {
                    if(!cachedLocaleData.containsKey(item.getKey())){
                        mySqlManager.execute("INSERT INTO " + table + " (l_key, value) VALUES(?, ?)", item.getKey(), item.getValue());
                    }
                });

                break;
            case PET:
                try {
                    while (result.next()) {
                        UUID uniqueId = UUID.fromString(result.getString("uniqueId"));
                        UUID ownerId = UUID.fromString(result.getString("owner"));
                        Optional<PetType> type = plugin.getStorageManager()
                                .getTypeByUniqueId(UUID.fromString(result.getString("type")));

                        if(!type.isPresent()){
                            plugin.getLogger().warning("Pet type specified by pet '" + uniqueId.toString() + "' is unavailable.");
                            return;
                        }

                        String name = result.getString("name");

                        IPet.IPetBuilder petBuilder = IPet.builder()
                                .uniqueId(uniqueId)
                                .owner(ownerId)
                                .type(type.get())
                                .name(name);

                        String particleString = result.getString("particle");
                        IParticle particle = null;
                        if(!particleString.isEmpty()){
                            particle = plugin.getGson().fromJson(particleString, IParticle.class);
                            particle.setPlugin(plugin);
                            petBuilder.particle(particle);
                        }

                        Pet pet = petBuilder.build();
                        storePet(pet, false);

                        if(particle != null){
                            particle.setPet(pet);
                        }

                    }
                } catch (SQLException ex){
                    plugin.getLogger().warning(provideExceptionErrorMessage(storageType));
                    ex.printStackTrace();
                }

                break;
            case TYPE:

                try {
                    while (result.next()) {
                        UUID uniqueId = UUID.fromString(result.getString("uniqueId"));
                        String name = result.getString("name");
                        String texture = result.getString("texture");
                        double price = 0;

                        if(!result.getString("price").isEmpty())
                            price = result.getDouble("price");

                        cachedTypes.add(new PetType(uniqueId, name, texture, price));
                        plugin.getLogger().info("Loaded type '" + name + "' by identifier '" + uniqueId + "'");
                    }
                } catch (SQLException ex){
                    plugin.getLogger().warning(provideExceptionErrorMessage(storageType));
                    ex.printStackTrace();
                }

                break;
            case MISC:

                try {
                    while (result.next()){
                        Material material = Material.valueOf(result.getString("material"));
                        int amount = result.getInt("amount");
                        double value = result.getDouble("value");

                        plugin.getLogger().info("Cached food item with material " + material.name());
                        cachedFoodItems.add(new Food(material, amount, value));
                    }
                } catch (SQLException ex){
                    plugin.getLogger().warning(provideExceptionErrorMessage(storageType));
                    ex.printStackTrace();
                }


                break;
            default:
        }
    }

    private void createTables(){

        String localeQuery = "" +
                "create table if not exists fp_locale (\n" +
                "    recordId int auto_increment\n" +
                "        primary key,\n" +
                "    l_key    text null,\n" +
                "    value    text not null\n" +
                ");";
        String petQuery = "" +
                "create table if not exists fp_pet (\n" +
                "    recordId int auto_increment\n" +
                "        primary key,\n" +
                "    uniqueId text not null,\n" +
                "    owner    text not null,\n" +
                "    type     text not null,\n" +
                "    name     text not null,\n" +
                "    particle text not null\n" +
                ");";
        String typeQuery = "" +
                "create table if not exists fp_type (\n" +
                "    recordId int auto_increment\n" +
                "        primary key,\n" +
                "    uniqueId text not null,\n" +
                "    name     text not null,\n" +
                "    texture  text not null,\n" +
                "    price    text not null\n" +
                ");";
        String miscQuery = "" +
                "create table if not exists fp_misc (\n" +
                "    recordId int auto_increment\n" +
                "        primary key,\n" +
                "    material text   not null,\n" +
                "    amount   int    not null,\n" +
                "    value    double not null\n" +
                ");";

        mySqlManager.execute(localeQuery);
        mySqlManager.execute(petQuery);
        mySqlManager.execute(typeQuery);
        mySqlManager.execute(miscQuery);

    }

    @Override
    public void storePet(Pet pet, boolean save) {

        cachedPets.add(pet);

        if(save){
            String particle = pet.hasParticle() ? plugin.getGson().toJson(pet.getParticle()) : "";
            mySqlManager.execute("INSERT INTO " + getTable("pet") + " (uniqueId, owner, type, name, particle) VALUES(?, ?, ?, ?, ?)",
                    pet.getUniqueId().toString(), pet.getOwner().toString(), pet.getType().getUniqueId().toString(), pet.getName(), particle);
        }

    }

    public List<LocaleItem> getDefaultLocaleValues(){
        List<LocaleItem> strings = new ArrayList<>();
        for(String key : defaultLocale.getKeys(false)){
            if(defaultLocale.isString(key)){
                strings.add(LocaleItem.builder().key(key).value(defaultLocale.getString(key)).build());
            } else {
                strings.addAll(getStringsFromSection(defaultLocale.getConfigurationSection(key), ""));
            }

        }

        return strings;
    }

    public List<LocaleItem> getStringsFromSection(ConfigurationSection section, String parent){
        List<LocaleItem> strings = new ArrayList<>();
        if(section == null)
            return strings;

        parent += (parent.isEmpty() ? "" : ".") + (section.getName().equals("locale") ? "" : section.getName());

        for(String key : section.getKeys(false)){

            if(section.isString(key)){
                strings.add(LocaleItem.builder().key(parent + "." + key).value(section.getString(key)).build());
            } else {
                if(section.isConfigurationSection(key)) {
                    strings.addAll(getStringsFromSection(section.getConfigurationSection(key), parent));
                }
            }

        }

        return strings;
    }

    private String getTable(String name){
        return prefix + name;
    }

    @Override
    public void updatePet(Pet pet, StorageManager.Action action) {
        switch (action){
            case REMOVE:{
                cachedPets.remove(pet);
                mySqlManager.execute("DELETE FROM " + getTable("pet") + " WHERE uniqueId = ?", pet.getUniqueId().toString());
                break;
            }

            case RENAME:{
                updateValue(pet, "name", pet.getName());
                break;
            }

            case PARTICLE:{
                if(!pet.hasParticle()){
                    updateValue(pet, "particle", "");
                    return;
                }

                updateValue(pet, "particle", plugin.getGson().toJson(pet.getParticle()));
                break;
            }

            default:{ }
        }
    }

    private void updateValue(Pet pet, String col, String value){
        mySqlManager.execute("UPDATE " +  getTable("pet") + " SET " + col + "=? WHERE uniqueId=?", value, pet.getUniqueId().toString());
    }

    private String provideExceptionErrorMessage(Type type){
        return String.format("An error occurred preloading %s", type.name().toLowerCase());
    }

    @Builder
    public static class LocaleItem {

        @Getter
        private final String key;
        @Getter
        private final String value;

    }

}
