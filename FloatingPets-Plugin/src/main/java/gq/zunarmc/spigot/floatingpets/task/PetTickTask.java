package gq.zunarmc.spigot.floatingpets.task;

import gq.zunarmc.spigot.floatingpets.FloatingPets;
import gq.zunarmc.spigot.floatingpets.api.model.Pet;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

public class PetTickTask implements Runnable {

    private final FloatingPets plugin;
    private final Pet pet;
    private final Player owner;

    private String lastTitle;
    private double lastHealth;

    public PetTickTask(FloatingPets plugin, Pet pet){
        this.plugin     = plugin;
        this.pet        = pet;
        this.owner      = pet.getOnlineOwner();
        this.lastHealth = pet.getEntity().getEntityHealth();
        this.lastTitle  = pet.getName();
    }

    @Override
    public void run() {
        if(pet == null || pet.getEntity() == null)
            return;

        tickMovement();
        tickAutomaticHat();
        tickChangeUpdate();
    }

    private void tickMovement(){
        if(pet.getNameTag() != null){
            plugin.getNmsHelper().getNmsManager()
                    .teleport((ArmorStand) pet.getNameTag(), pet.getEntity().getEntity());
        }
    }

    private void tickAutomaticHat(){
        if(!owner.isOnGround() && !pet.getEntity().getEntity().isLeashed()){
            int dist = plugin.getUtility().getDistanceFromGround(owner);
            if(!owner.getPassengers().contains(pet.getNameTag())
                    && !pet.getEntity().getEntity().getPassengers().contains(owner)
                    && dist >= 8){

                owner.addPassenger(pet.getNameTag());
                pet.getNameTag().addPassenger(pet.getEntity().getEntity());
                return;
            }

            if(dist > 2 && dist <= 4){
                owner.removePassenger(pet.getNameTag());
                pet.getNameTag().removePassenger(pet.getEntity().getEntity());
            }
        }
    }

    private void tickChangeUpdate(){
        if(hasChanged()) {
            if (pet.getEntity().getEntityHealth() != lastHealth) {
                lastHealth = pet.getEntity().getEntityHealth();
            }

            if (!pet.getName().equals(lastTitle)) {
                lastTitle = pet.getName();
            }

            pet.getNameTag().setCustomName(plugin.getUtility().formatTitle(pet,
                    owner.hasPermission("floatingpets.name.color")));
        }
    }

    private boolean hasChanged(){
        return pet.getEntity().getEntityHealth() != lastHealth || !pet.getName().equals(lastTitle);
    }

}