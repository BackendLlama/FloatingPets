package net.llamasoftware.spigot.floatingpets.nms.v1_15_R1;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import net.llamasoftware.spigot.floatingpets.api.model.FloatingPet;
import net.llamasoftware.spigot.floatingpets.api.model.Pet;
import net.llamasoftware.spigot.floatingpets.api.model.Setting;
import net.llamasoftware.spigot.floatingpets.api.nms.NMSManager;
import net.llamasoftware.spigot.floatingpets.nms.v1_15_R1.pet.FloatingPet_v1_15_R1;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"rawtypes", "unused"})
public class NMSManagerImpl implements NMSManager {

    private EntityTypes entityStore;

    @Override @SuppressWarnings("unchecked")
    public void registerEntity() {
        String customName = "floatingpet";
        Map<String, Type<?>> types = (Map<String, Type<?>>) DataConverterRegistry.a()
                .getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion()))
                .findChoiceType(DataConverterTypes.ENTITY)
                .types();

        types.put("minecraft:" + customName, types.get("minecraft:cat"));
        EntityTypes.a<Entity> a = EntityTypes.a.a(FloatingPet_v1_15_R1::new, EnumCreatureType.CREATURE);
        entityStore = IRegistry.a(IRegistry.ENTITY_TYPE, customName, a.a(customName));
    }

    @Override
    public FloatingPet constructPet(Location location, Player onlineOwner, Pet pet, Map<Setting, String> settings) {
        World world = location.getWorld();
        if(world == null)
            return null;

        Entity b = entityStore.spawnCreature(((CraftWorld) world).getHandle(),
                null,
                null,
                null,
                new BlockPosition(location.getX(), location.getY(), location.getZ()),
                null, false, false);

        FloatingPet_v1_15_R1 nmsPet = (FloatingPet_v1_15_R1) b;
        if(nmsPet == null)
            return null;

        nmsPet.construct(location, onlineOwner, pet, settings);
        return (FloatingPet_v1_15_R1) b;
    }

    @Override
    public ItemStack getItemStackFromTexture(String texture){
        ItemStack stack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) stack.getItemMeta();
        if(skullMeta == null)
            return null;

        GameProfile skinProfile = new GameProfile(UUID.randomUUID(), null);
        skinProfile.getProperties().put("textures", new Property("textures",
                Base64Coder.encodeString("{textures:{SKIN:{url:\"" + texture + "\"}}}")));

        Field field;
        try {
            field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(skullMeta, skinProfile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex){
            ex.printStackTrace();
        }

        stack.setItemMeta(skullMeta);
        return stack;
    }

    @Override
    public void killPets() {
        Bukkit.getWorlds().forEach(world -> world.getEntities().forEach(entity -> {
            CraftEntity ent = (CraftEntity) entity;
            if(ent.getHandle() instanceof FloatingPet_v1_15_R1 || ent.hasMetadata("FloatingPets_Pet")){
                ent.remove();
                ((FloatingPet_v1_15_R1) ent.getHandle()).kill();
            }
        }));
    }

    @Override
    public void teleport(org.bukkit.entity.ArmorStand nameTag, org.bukkit.entity.Entity entity) {
        EntityArmorStand handle = ((CraftArmorStand) nameTag).getHandle();
        Entity pet = ((CraftEntity) entity).getHandle();
        handle.setLocation(pet.locX(), pet.locY(), pet.locZ(), pet.yaw, pet.pitch);
        handle.setHeadRotation(pet.yaw);
    }

}