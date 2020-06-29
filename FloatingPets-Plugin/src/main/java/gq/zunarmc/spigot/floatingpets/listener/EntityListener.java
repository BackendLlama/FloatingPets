package gq.zunarmc.spigot.floatingpets.listener;

import gq.zunarmc.spigot.floatingpets.Constants;
import gq.zunarmc.spigot.floatingpets.FloatingPets;
import gq.zunarmc.spigot.floatingpets.locale.Locale;
import gq.zunarmc.spigot.floatingpets.manager.storage.StorageManager;
import gq.zunarmc.spigot.floatingpets.api.model.Pet;
import gq.zunarmc.spigot.floatingpets.api.model.Setting;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class EntityListener implements Listener {

    private final FloatingPets plugin;

    public EntityListener(FloatingPets plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event){
        if(!(event.getEntity() instanceof LivingEntity))
            return;

        LivingEntity entity = (LivingEntity) event.getEntity();
        Optional<Pet> pet = plugin.getPetManager().getPetByEntity(entity, false);

        if(!pet.isPresent() || !pet.get().isAlive()) {
            return;
        }

        if(!plugin.isSetting(Setting.PET_HEALTH)){
            event.setDamage(0);
            event.setCancelled(true);
            return;
        }

        boolean cancel = false;
        switch (event.getCause()){
            case FALL:{
                cancel = !plugin.isSetting(Setting.PET_DAMAGE_BY_FALL);

                if(pet.get().hasPassenger(pet.get().getOnlineOwner())){
                    event.setDamage(0);
                    cancel = true;
                }

                break;
            }

            case VOID:{
                cancel = !plugin.isSetting(Setting.PET_DAMAGE_BY_VOID);
                break;
            }

            default:
                break;
        }

        if(cancel) // Having no value is more applicable to context
            event.setCancelled(true);

    }

    @EventHandler
    public void onEntityPortal(EntityPortalEvent event){
        Optional<Pet> pet = plugin.getPetManager().getPetByEntity(event.getEntity(), false);
        if(pet.isPresent()){
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreatureSpawn(CreatureSpawnEvent event){
        LivingEntity entity = event.getEntity();

        if(entity.hasMetadata(Constants.METADATA_PET)
                || entity.hasMetadata(Constants.METADATA_NAME_TAG)) {
            event.setCancelled(false);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){

        LivingEntity entity = event.getEntity();
        Optional<Pet> pet = plugin.getPetManager().getPetByEntity(entity, true);

        if(!pet.isPresent())
            return;

        plugin.getPetManager().despawnPet(pet.get());

        event.setDroppedExp(0);
        event.getDrops().clear();

        if(!plugin.isSetting(Setting.PET_HEALTH)){
            return;
        }

        if(plugin.isSetting(Setting.PET_RESPAWN_ON_DEATH)){
            plugin.getPetManager().spawnPet(pet.get(), pet.get().getOnlineOwner().getLocation(),
                    pet.get().getOnlineOwner(), false);
        } else {
            if(plugin.isSetting(Setting.PET_REMOVE_ON_DEATH)){
                plugin.getStorageManager().updatePet(pet.get(), StorageManager.Action.REMOVE);
            }
        }

        plugin.getLocale().send(pet.get().getOnlineOwner(), "pet.died", false,
                new Locale.Placeholder("type", pet.get().getType().getName()));

        if(plugin.isSetting(Setting.PET_DEATH_MESSAGES)){
            Player killer = entity.getKiller();
            if(killer == null)
                return;

            List<String> deathMessages = plugin.getStorageManager().getLocaleListByKey("pet.death-messages");
            String message = deathMessages.get(ThreadLocalRandom.current().nextInt(deathMessages.size()));

            Bukkit.getOnlinePlayers().forEach(player -> plugin.getLocale().send(player, message,
                    false, new Locale.Placeholder("display_name", pet.get().getName()),
                    new Locale.Placeholder("killer", player.getName())));

        }

    }

    @EventHandler
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event){
        ArmorStand armorStand = event.getRightClicked();
        if(!armorStand.hasMetadata(Constants.METADATA_NAME_TAG))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){

        Entity damager = event.getDamager();
        Entity entity = event.getEntity();

        if(!plugin.isSetting(Setting.PET_HEALTH)) {
            if (entity instanceof LivingEntity
                    && plugin.isPet((LivingEntity) entity)) {

                event.setCancelled(true);
                return;
            }
        }

        if(entity.hasMetadata(Constants.METADATA_PET)){
            Optional<Pet> pet = plugin.getPetManager().getPetByEntity(entity, true);
            if(!pet.isPresent())
                return;

            if(!plugin.isSetting(Setting.PET_DAMAGE_BY_ATTACK)){
                event.setCancelled(true);
                return;
            }

            if(damager instanceof Player){
                if(damager.getUniqueId().equals(pet.get().getOwner())){
                    if(!plugin.isSetting(Setting.PET_DAMAGE_ATTACKED_BY_OWNER)) {
                        event.setCancelled(true);
                        return;
                    }
                } else {
                    if(!plugin.isSetting(Setting.PET_DAMAGE_ATTACKED_BY_PLAYER)) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }

            if(entity instanceof LivingEntity && ((LivingEntity) entity).getHealth() - event.getDamage() <= 0){
                Bukkit.getPluginManager()
                        .callEvent(new EntityDeathEvent(pet.get().getEntity().getEntity(), new ArrayList<>()));
                return;
            }

            if(pet.get().getOwner().equals(damager.getUniqueId())){
                entity.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, pet.get().getLocation(),
                        1, 0.0, 0.0, 0.0);
            }

        }

        if(damager.hasMetadata(Constants.METADATA_PET)){
            if(entity instanceof Player){
                Player player = (Player) entity;
                Optional<Pet> pet = plugin.getPetManager().getPetByEntity(damager, false);

                if(!pet.isPresent())
                    return;

                if(pet.get().getOwner().equals(player.getUniqueId())){ // ??? && !plugin.isSetting(Setting.PET_DAMAGE_ATTACKED_BY_OWNER)
                    event.setCancelled(true);
                }

                if(!event.isCancelled()){
                    World world = pet.get().getLocation().getWorld();
                    if(world != null) {
                        world.spawnParticle(Particle.SWEEP_ATTACK, pet.get().getLocation(), 1, 0, 0, 0, 0);
                    }
                }

            }
        }

    }

}