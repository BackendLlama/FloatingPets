package gq.zunarmc.spigot.floatingpets.external.wg;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gq.zunarmc.spigot.floatingpets.FloatingPets;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class WGManager {

    private final FloatingPets plugin;
    private final WorldGuard worldGuard;

    public WGManager(FloatingPets plugin){
        this.plugin = plugin;
        worldGuard = WorldGuard.getInstance();
    }

    public void allowSpawn(Location location){
        World bukkitWorld = location.getWorld();
        if(bukkitWorld == null)
            return;

        RegionManager manager = worldGuard.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(bukkitWorld));
        if(manager == null)
            return;

        ProtectedRegion globalRegion = manager.getRegions().get("__global__");
        if(globalRegion != null && isMobSpawningDenied(globalRegion)){
            toggleMobSpawning(globalRegion);
        }

        ApplicableRegionSet applicableRegions = manager.getApplicableRegions(BukkitAdapter.asBlockVector(location));
        applicableRegions.getRegions().stream()
                .filter(this::isMobSpawningDenied)
                .forEach(this::toggleMobSpawning);

    }

    public void toggleMobSpawning(ProtectedRegion region){
        region.setFlag(Flags.MOB_SPAWNING, StateFlag.State.ALLOW);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, ()
                -> region.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY), 2L);
    }

    public boolean isMobSpawningDenied(ProtectedRegion region){
        StateFlag.State flag = region.getFlag(Flags.MOB_SPAWNING);
        return flag == StateFlag.State.DENY;
    }

}
