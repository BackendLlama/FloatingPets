package net.llamasoftware.spigot.floatingpets.nms.v1_15_R1.pet;

import net.llamasoftware.spigot.floatingpets.api.model.FloatingPet;
import net.llamasoftware.spigot.floatingpets.api.model.Pet;
import net.llamasoftware.spigot.floatingpets.api.model.Setting;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Cat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.*;

public class FloatingPet_v1_15_R1 extends EntityCat implements FloatingPet {

    private Pet pet;
    private Location location;
    private Player onlineOwner;
    private Map<Setting, String> settings;
    private long latestTick;

    @SuppressWarnings({"unused", "rawtypes"})
    public FloatingPet_v1_15_R1(EntityTypes types, World world) {
        super(EntityTypes.CAT, world);
    }

    public void construct(Location location, Player onlineOwner, Pet pet, Map<Setting, String> settings){
        this.location    = location;
        this.onlineOwner = onlineOwner;
        this.pet         = pet;
        this.settings    = settings;
        this.transformEntity();
    }

    @Override
    public void ejectPassengers(){
        ListIterator<Entity> entities = passengers.listIterator();
        if(entities.hasNext()){
            Entity entity = entities.next();
            if(entity.getBukkitEntity().getType() == EntityType.PLAYER) {
                entities.remove();
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        latestTick = System.currentTimeMillis();
        /* Let pet spawn when difficulty is peaceful
        if (!this.world.isClientSide && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
            this.die();
        }*/
    }

    @Override
    public void die(){
        if(System.currentTimeMillis() - latestTick > 300){
            super.die();
        }
    }

    public void kill(){
        super.die();
    }

    @Override
    public EntityTypes<?> getEntityType() {
        return EntityTypes.CAT;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void transformEntity(){
        setPosition(location.getX(), location.getY(), location.getZ());
        setSilent(true);
        setInvisible(true);
        H = 1;

        this.getAttributeInstance(GenericAttributes.MAX_HEALTH)
                .setValue(Double.parseDouble(settings.get(Setting.PET_MAX_HEALTH)));

        setHealth((float) Double.parseDouble(settings.get(Setting.PET_DEFAULT_HEALTH)));

        addEffect(new MobEffect(MobEffects.INVISIBILITY,
                Integer.MAX_VALUE, 1, false, false));

        Plugin plugin = Bukkit.getPluginManager().getPlugin("FloatingPets");
        Cat cat = (Cat) getBukkitEntity();
        if(plugin == null || cat == null)
            return;

        cat.setMetadata("FloatingPets_Pet", new FixedMetadataValue(plugin, onlineOwner.getUniqueId()));
        cat.setSilent(true);

        Set goalD       = (Set) getSelector("d", goalSelector);
        Set targetD     = (Set) getSelector("d", targetSelector);
        EnumMap goalC   = (EnumMap) getSelector("c", goalSelector);
        EnumMap targetC = (EnumMap) getSelector("c", targetSelector);

        goalD.clear(); goalC.clear();
        targetD.clear(); targetC.clear();

        goalSelector.a(0, new PathfinderGoalFloat(this));
        goalSelector.a(0, new PathfinderGoalMeleeAttack(this, 1.2D, true));
        goalSelector.a(0, new PathfinderGoalLeapAtTarget(this, 0.7F));

        if(isSetting(Setting.PET_DAMAGE_BY_ATTACK)) {
            goalSelector.a(0, new net.llamasoftware.spigot.floatingpets.nms.v1_15_R1.pathfinder.PathfinderGoalOwnerHurtByTarget(this,
                    ((CraftPlayer) onlineOwner).getHandle()));
            goalSelector.a(0, new net.llamasoftware.spigot.floatingpets.nms.v1_15_R1.pathfinder.PathfinderGoalOwnerHurtTarget(this,
                    ((CraftPlayer) onlineOwner).getHandle()));
        }

        goalSelector.a(7, new PathfinderGoalRandomStrollLand(this, 1.0D));

        goalSelector.a(8, new net.llamasoftware.spigot.floatingpets.nms.v1_15_R1.pathfinder.PathfinderGoalFollowOwner(this, onlineOwner, 1.2));
        goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        goalSelector.a(8, new PathfinderGoalRandomLookaround(this));

    }

    private Object getSelector(String name, Object object){
        Field field;
        Object o = null;
        try {
            field = PathfinderGoalSelector.class.getDeclaredField(name);
            field.setAccessible(true);
            o = field.get(object);
        } catch (NoSuchFieldException| IllegalAccessException ex){
            ex.printStackTrace();
        }

        return o;
    }

    @Override
    public void spawnPet(Location location) {
        if(location.getWorld() == null)
            return;

        WorldServer wb = ((CraftWorld) location.getWorld()).getHandle();
        wb.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    @Override
    public Pet getPet() {
        return pet;
    }

    @Override
    public boolean hasTarget() {
        return getGoalTarget() != null && getGoalTarget().isAlive();
    }

    @Override
    public boolean hasTarget(org.bukkit.entity.Entity entity) {
        return hasTarget() && Objects.requireNonNull(getGoalTarget())
                .getBukkitEntity().getUniqueId().equals(entity.getUniqueId());
    }

    @Override
    public void removeTarget() {
        this.setGoalTarget(null);
    }

    @Override
    public double getEntityHealth() {
        return getHealth();
    }

    @Override
    public String getSetting(Setting setting) {
        return settings.get(setting);
    }

    @Override
    public boolean isSetting(Setting setting) {
        return Boolean.parseBoolean(getSetting(setting));
    }

    @Override
    public void teleportToOwner() {
        setLocation(onlineOwner.getLocation().getX(), onlineOwner.getLocation().getY(), onlineOwner.getLocation().getZ(), 0, 0);
    }

    @Override
    public LivingEntity getEntity() {
        return (LivingEntity) getBukkitEntity();
    }

}