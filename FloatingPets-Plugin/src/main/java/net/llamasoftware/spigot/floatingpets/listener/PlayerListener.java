package net.llamasoftware.spigot.floatingpets.listener;

import net.llamasoftware.spigot.floatingpets.FloatingPets;
import net.llamasoftware.spigot.floatingpets.api.model.Pet;
import net.llamasoftware.spigot.floatingpets.api.model.Setting;
import net.llamasoftware.spigot.floatingpets.api.model.Skill;
import net.llamasoftware.spigot.floatingpets.menu.MenuPetStorage;
import net.llamasoftware.spigot.floatingpets.model.misc.Food;
import net.llamasoftware.spigot.floatingpets.model.skill.StorageSkill;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class PlayerListener implements Listener {

    private final FloatingPets plugin;

    public PlayerListener(FloatingPets plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        if(!plugin.isSetting(Setting.PET_SPAWN_ON_JOIN))
            return;

        Player player = event.getPlayer();
        List<Pet> pets = plugin.getStorageManager().getPetsByOwner(player.getUniqueId());

        pets.forEach(pet -> plugin.getPetManager()
                .spawnPet(pet, player.getLocation(), player, true));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){

        Player player = event.getPlayer();
        List<Pet> pets = plugin.getStorageManager().getPetsByOwner(player.getUniqueId());

        pets.forEach(pet -> plugin.getPetManager().despawnPet(pet));
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event){
        if(event.getHand() != EquipmentSlot.HAND)
            return;

        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        Optional<Pet> pet = plugin.getPetManager().getPetByEntity(entity, false);

        if(!pet.isPresent() || !pet.get().getOwner().equals(player.getUniqueId())) {
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        Optional<Food> foodItem = plugin.getStorageManager().getFoodItemByStack(item);

        if(plugin.isSetting(Setting.PET_HEALING) && foodItem.isPresent()){

            double value = foodItem.get().getValue();
            LivingEntity livingPet = pet.get().getEntity().getEntity();
            AttributeInstance maxHealth = livingPet.getAttribute(Attribute.GENERIC_MAX_HEALTH);

            double newHealth = livingPet.getHealth() + value;

            if(maxHealth != null) {
                if (livingPet.getHealth() >= maxHealth.getValue()) {
                    return;
                }

                if (newHealth > maxHealth.getValue()) {
                    newHealth = maxHealth.getValue();
                }
            }

            if(maxHealth == null || newHealth <= maxHealth.getValue()) {
                item.setAmount(item.getAmount() - 1);
                livingPet.setHealth(newHealth);
                livingPet.getWorld().spawnParticle(Particle.HEART, livingPet.getLocation(), 1, 0, 0, 0);
            }

        } else if(!foodItem.isPresent()){
            if(player.isSneaking()){
                Optional<Skill> skill = pet.get().getSkillOfType(Skill.Type.STORAGE);
                if(skill.isPresent()) {
                    StorageSkill storageSkill = (StorageSkill) skill.get();
                    plugin.getMenuManager().openMenu(player, new MenuPetStorage("Pet Storage",
                            storageSkill.getRows(), pet.get()), plugin);
                }
            }
        }

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        LinkedList<Pet> pets = plugin.getPetManager().getPetsByOwner(event.getEntity());
        pets.forEach(pet -> {
            pet.getNameTag().leaveVehicle();
            pet.getEntity().getEntity().leaveVehicle();
            pet.getEntity().removeTarget();
        });
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event){

        List<Pet> pets = plugin.getPetManager().getPetsByOwner(event.getPlayer());
        for(Pet pet : pets) {
            Player player = event.getPlayer();

            World to = event.getPlayer().getWorld();

            /*
            if (!pet.isPresent()) {
                if (plugin.isSetting(Setting.WORLD_FILTER) &&
                        plugin.getConfigDefinition().isExcludedWorld(from.getName()) &&
                        plugin.isSetting(Setting.WORLD_FILTER_RESPAWN)) {

                    Optional<Pet> stored = plugin.getStorageManager().getPetsByOwner(player.getUniqueId());

                    if (!stored.isPresent())
                        return;

                    plugin.getPetManager().spawnPet(stored.get(), player.getLocation(),
                            player, true);

                }

                return;
            }*/

            plugin.getPetManager().despawnPet(pet);

            if (plugin.isSetting(Setting.WORLD_FILTER)) {
                if (plugin.getConfigDefinition().isExcludedWorld(to.getName())) {
                    plugin.getLocale().send(player, "&7Your pet was despawned because it isn't allowed in this world.", true);
                    return;
                }
            }

            plugin.getPetManager().spawnPet(pet, player.getLocation(), player, false);
        }

    }

}