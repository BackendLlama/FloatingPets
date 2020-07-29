package net.llamasoftware.spigot.floatingpets.helper;

import net.llamasoftware.spigot.floatingpets.Constants;
import net.llamasoftware.spigot.floatingpets.api.model.FloatingPet;
import net.llamasoftware.spigot.floatingpets.api.model.Pet;
import net.llamasoftware.spigot.floatingpets.api.model.Setting;
import net.llamasoftware.spigot.floatingpets.api.nms.NMSManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NMSHelper {

    @Getter
    private NMSManager nmsManager;

    public NMSHelper(){
        try {
            if(getCurrentVersion() != null) {
                nmsManager = (NMSManager) Class.forName("net.llamasoftware.spigot.floatingpets.nms." + getCurrentVersion().name() + ".NMSManagerImpl")
                                                                                                                .getConstructor().newInstance();
            } else {
                System.out.println(Bukkit.getServer().getClass().getPackage().getName() + " is not supported by FloatingPets.");
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public FloatingPet constructPet(Location location, Player owner, Pet pet, Map<Setting, String> settings){

        return nmsManager.constructPet(location, owner, pet, settings);
    }

    public void killPets(){
        List<Entity> entities = Bukkit.getWorlds().stream()
                .map(World::getEntities)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        for (Entity entity : entities) {
            if(entity.hasMetadata(Constants.METADATA_NAME_TAG)
                    || entity.hasMetadata(Constants.METADATA_PET)){

                entity.remove();
            }
        }
    }

    public ItemStack getItemStackFromTexture(String texture){
        return nmsManager.getItemStackFromTexture(texture);
    }

    public void registerCustomPet(){
        nmsManager.registerEntity();
    }

    @SuppressWarnings("unused")
    private enum NMSVersion {
        /*v1_8_R1,
        v1_8_R2,
        v1_8_R3,
        v1_9_R1,
        v1_9_R2,
        v1_10_R1,
        v1_11_R1,*/
        v1_12_R1,
        /*v1_13_R1,
        v1_13_R2,
        v1_14_R1, We haven't added support for these yet */
        v1_15_R1,
        v1_16_R1
    }

    private NMSVersion getCurrentVersion(){
        Matcher matcher = Pattern.compile("v\\d+_\\d+_R\\d+").matcher(Bukkit.getServer().getClass().getPackage().getName());
        if (matcher.find()) {
            return NMSVersion.valueOf(matcher.group());
        } else {
            return null;
        }
    }

}