package net.llamasoftware.spigot.floatingpets.api.nms;

import net.llamasoftware.spigot.floatingpets.api.model.Pet;
import net.llamasoftware.spigot.floatingpets.api.model.FloatingPet;
import net.llamasoftware.spigot.floatingpets.api.model.Setting;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface NMSManager {

    void registerEntity();

    FloatingPet constructPet(Location location, Player onlineOwner, Pet pet, Map<Setting, String> settings);

    ItemStack getItemStackFromTexture(String texture);

    void killPets();

    void teleport(org.bukkit.entity.ArmorStand nameTag, Entity entity);
}