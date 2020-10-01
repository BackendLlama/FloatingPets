package net.llamasoftware.spigot.floatingpets.manager.storage;

import net.llamasoftware.spigot.floatingpets.FloatingPets;
import net.llamasoftware.spigot.floatingpets.api.model.Pet;
import net.llamasoftware.spigot.floatingpets.api.model.PetType;
import net.llamasoftware.spigot.floatingpets.api.model.Setting;
import net.llamasoftware.spigot.floatingpets.locale.Locale;
import net.llamasoftware.spigot.floatingpets.model.misc.Cooldown;
import net.llamasoftware.spigot.floatingpets.model.misc.Food;
import net.llamasoftware.spigot.floatingpets.model.pet.IPet;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class StorageManager {

    protected final LinkedList<Pet> cachedPets;
    @Getter
    public final List<PetType> cachedTypes;
    protected final List<Food> cachedFoodItems;
    protected final Map<String, Object> cachedLocaleData;

    private final FloatingPets plugin;

    protected StorageManager(FloatingPets plugin) {
        cachedPets       = new LinkedList<>();
        cachedTypes      = new ArrayList<>();
        cachedFoodItems  = new ArrayList<>();
        cachedLocaleData = new HashMap<>();
        this.plugin = plugin;
    }

    public void load(){
        setup();
        cachedFoodItems.clear();
        cachedPets.clear();
        cachedTypes.clear();
        cachedLocaleData.clear();

        Arrays.stream(StorageManager.Type.values())
                .filter(plugin::isPreload)
                .forEach(this::preload);
    }

    public abstract void setup();

    public abstract void preload(Type type);

    /* Pet storage */

    public LinkedList<Pet> getPetsByOwner(UUID uniqueId) {
        Supplier<Stream<Pet>> streamSupplier = () -> cachedPets.stream()
                .filter(pet -> pet.getOwner().equals(uniqueId));

        LinkedList<Pet> collect = streamSupplier.get()
                .filter(Pet::isAlive)
                .sorted(Comparator.comparingInt(o -> o.getEntity().getEntity().getEntityId()))
                .collect(Collectors.toCollection(LinkedList::new));

        collect.addAll(streamSupplier.get()
                .filter(pet -> !pet.isAlive())
                .sorted(Comparator.comparingInt(Object::hashCode))
                .collect(Collectors.toCollection(LinkedList::new)));

        return collect;
    }

    public void selectPet(Player player, PetType type){

        boolean settingCooldown = plugin.isSetting(Setting.PET_COOLDOWN_SELECT);
        Optional<Cooldown> cooldown = plugin.getCooldownManager()
                .getCooldown(player.getUniqueId(), Cooldown.Type.SELECT);

        List<Pet> currentPets = plugin.getStorageManager().getPetsByOwner(player.getUniqueId());
        Locale locale = plugin.getLocale();

        if(settingCooldown) {
            if (cooldown.isPresent()) {
                locale.send(player, "cooldown.timeout", false,
                        new Locale.Placeholder("time", String.valueOf(cooldown.get().getTimeLeft() / 1000)));

                return;
            }
        }

        if(!plugin.isSetting(Setting.MULTIPLE_PETS)
                && !plugin.getPetManager().getPetsByOwner(player).isEmpty()) {

            Pet currentPet = currentPets.get(0);
            locale.send(player, "commands.select.removed-current", true);
            plugin.getPetManager().despawnPet(currentPet);
            plugin.getStorageManager().updatePet(currentPet, StorageManager.Action.REMOVE);
        }

        if(plugin.isSetting(Setting.MULTIPLE_PETS) && plugin.getStorageManager()
                .getPetsByOwner(player.getUniqueId()).size()
                >= plugin.getUtility().getPermissionBasedSetting(player, "pet.multiple_pets.limits",
                "limit", Long.MAX_VALUE)){

            locale.send(player, "commands.select.pet-limit", true);
            return;
        }

        if(plugin.getConfigDefinition().isExcludedWorld(player.getWorld().getName())){
            locale.send(player, "generic.world-restricted", false);
            return;
        }

        Optional<Pet> current = plugin.getStorageManager().getPetsByOwner(player.getUniqueId())
                .stream().filter(p -> p.getType() == type).findAny();

        if(current.isPresent()){
            locale.send(player, "commands.select.removed-current", true);
            plugin.getPetManager().despawnPet(current.get());
            plugin.getStorageManager().updatePet(current.get(), StorageManager.Action.REMOVE);
        }

        plugin.getPetManager().spawnPet(createPet(type, player), player.getLocation(), player, true);

        if(settingCooldown) {
            long expiry = System.currentTimeMillis()
                    + 1000 * plugin.getUtility().getPermissionBasedSetting(player, "pet.cooldown.select.limits",
                    "select_cooldown", 0);

            plugin.getCooldownManager().addCooldown(player.getUniqueId(), Cooldown.Type.SELECT, expiry);
        }
    }

    public Pet createPet(PetType type, OfflinePlayer offlinePlayer){

        Locale locale = plugin.getLocale();

        Pet pet = IPet.builder()
                .uniqueId(UUID.randomUUID())
                .name(locale.transformPlaceholders(plugin.getStringSetting(Setting.PET_NAME_DEFAULT_NAME),
                        new Locale.Placeholder("owner", offlinePlayer.getName()),
                        new Locale.Placeholder("type", type.getName())))
                .owner(offlinePlayer.getUniqueId())
                .type(type)
                .skills(new ArrayList<>())
                .extra(new HashMap<>())
                .plugin(plugin)
                .build();

        storePet(pet, true);

        return pet;
    }

    public abstract void storePet(Pet pet, boolean save);

    public abstract void updatePet(Pet pet, StorageManager.Action action);

    /* Type storage */

    public abstract void storeType(PetType type);

    public abstract void removeType(PetType type);

    public Optional<PetType> getTypeByName(String name){
        return cachedTypes.stream()
                .filter(petType -> petType.getName().equalsIgnoreCase(name))
                .findAny();
    }

    public Optional<PetType> getTypeByUniqueId(UUID uniqueId) {
        return cachedTypes.stream()
                .filter(petType -> petType.getUniqueId().equals(uniqueId))
                .findAny();
    }

    /* Locale */

    public String getLocaleByKey(String key) {
        if(!cachedLocaleData.containsKey(key))
            return key;

        return (String) cachedLocaleData.get(key);
    }

    @SuppressWarnings("unchecked")
    public List<String> getLocaleListByKey(String key) {
        if(!cachedLocaleData.containsKey(key))
            return new ArrayList<>();

        return (List<String>) cachedLocaleData.get(key);
    }

    /* Misc storage */

    public Optional<Food> getFoodItemByStack(ItemStack stack) {
        return cachedFoodItems.stream()
                .filter(foodItem -> foodItem.getMaterial() == stack.getType())
                .filter(foodItem -> stack.getAmount()      >= foodItem.getAmount())
                .findAny();
    }

    /* Model */

    public enum Type {
        LOCALE,
        TYPE,
        PET,
        MISC
    }

    public enum Action {
        RENAME,
        REMOVE,
        PARTICLE,
        SKILL,
        EXTRA
    }

}