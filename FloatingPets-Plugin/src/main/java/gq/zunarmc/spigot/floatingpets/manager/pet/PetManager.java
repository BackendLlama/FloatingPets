package gq.zunarmc.spigot.floatingpets.manager.pet;

import gq.zunarmc.spigot.floatingpets.Constants;
import gq.zunarmc.spigot.floatingpets.FloatingPets;
import gq.zunarmc.spigot.floatingpets.api.model.FloatingPet;
import gq.zunarmc.spigot.floatingpets.api.model.Pet;
import gq.zunarmc.spigot.floatingpets.api.model.Setting;
import gq.zunarmc.spigot.floatingpets.locale.Locale;
import gq.zunarmc.spigot.floatingpets.task.PetHealthRegenerationTask;
import gq.zunarmc.spigot.floatingpets.task.PetTickTask;
import gq.zunarmc.spigot.floatingpets.util.NBTEditor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PetManager {

    private final FloatingPets plugin;
    @Getter
    private final List<Pet> activePets;

    public PetManager(FloatingPets plugin){
        this.plugin     = plugin;
        this.activePets = new ArrayList<>();
    }

    public void spawnPet(Pet pet, Location location, Player onlineOwner, boolean message){

        if(location.getWorld() == null)
            return;

        if(!plugin.isSetting(Setting.MULTIPLE_PETS)) {
            List<Pet> previous = getPetByOwner(onlineOwner);
            if(!previous.isEmpty())
                despawnPet(previous.get(0));
        }

        if(plugin.getConfigDefinition().isExcludedWorld(location.getWorld().getName())){
            return;
        }

        if(plugin.getWgManager() != null){
            plugin.getWgManager().allowSpawn(location);
        }

        FloatingPet floatingPet = plugin.getNmsHelper().constructPet(location, onlineOwner, pet, plugin.getSettingsMap());

        World world = location.getWorld();
        if(world == null)
            return;

        ArmorStand nameTag = onlineOwner.getWorld().spawn(location, ArmorStand.class);
        nameTag.setSmall(true);
        nameTag.setVisible(false);
        nameTag.setCustomNameVisible(true);
        nameTag.setMetadata(Constants.METADATA_NAME_TAG, new FixedMetadataValue(plugin, pet.getUniqueId()));
        nameTag.setCustomName(pet.getName());
        nameTag.setInvulnerable(true);
        if(nameTag.getEquipment() == null)
            return;

        nameTag.getEquipment()
                .setHelmet(plugin.getNmsHelper().getItemStackFromTexture(pet.getType().getTexture()));

        if(floatingPet == null)
            return;

        floatingPet.getEntity().setMetadata(Constants.METADATA_PET,
                new FixedMetadataValue(plugin, pet.getUniqueId()));

        pet.setNameTag(nameTag);
        pet.setEntity(floatingPet);
        pet.attachNameTag();

        NBTEditor.set(pet.getEntity().getEntity(), 1, "FPComponent");
        NBTEditor.set(nameTag, 1, "FPComponent");

        if(pet.hasParticle())
            pet.getParticle().start();

        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,
                new PetTickTask(plugin, pet), 0, 1L);

        if(plugin.isSetting(Setting.PET_HEALTH)) {
            plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,
                    new PetHealthRegenerationTask(pet), 0, 20 * 2);
        }

        nameTag.setCustomName(plugin.getUtility().formatTitle(pet, onlineOwner.hasPermission("floatingpets.name.color")));
        activePets.add(pet);

        if(message){
            plugin.getLocale().send(onlineOwner, "generic.spawned",
                    true, new Locale.Placeholder("type", pet.getType().getName()),
                    new Locale.Placeholder("name", pet.getName()));
        }

    }

    public void despawnPet(Pet pet){
        if(pet.getEntity() == null)
            return;

        activePets.remove(pet);
        pet.getEntity().kill();
        pet.remove();
    }

    public void despawnPets(){
        activePets.forEach(Pet::remove);
        activePets.clear();
    }

    public boolean isPetSpawned(Pet pet){
        return activePets.contains(pet) && pet.isAlive();
    }

    public LinkedList<Pet> getPetByOwner(Player player){
        return activePets.stream()
                .filter(pet -> pet.getOnlineOwner() != null
                        && pet.getOnlineOwner().getUniqueId().equals(player.getUniqueId()))
                .sorted(Comparator.comparingInt(o -> o.getEntity().getEntity().getEntityId()))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public Optional<Pet> getPetByEntity(Entity entity, boolean specific) {
        Stream<Pet> petStream = activePets.stream()
                .filter(Objects::nonNull)
                .filter(pet -> pet.getEntity() != null);

        if(!specific){
            petStream = petStream.filter(pet -> pet.getEntity().getEntity().getEntityId() == entity.getEntityId()
                    || pet.getNameTag().getEntityId() == entity.getEntityId());

        } else {
            petStream = petStream.filter(pet -> pet.getEntity().getEntity().getEntityId() == entity.getEntityId());
        }

        return petStream.findAny();
    }

}