package net.llamasoftware.spigot.floatingpets.nms.v1_15_R1.pet;

import net.minecraft.server.v1_15_R1.Packet;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_15_R1.PacketPlayOutSpawnEntityLiving;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public final class PacketUtil {

    public static void sendDestroyPacket(Player player, Entity entity){
        sendPacket(player, new PacketPlayOutEntityDestroy(entity.getEntityId()));
    }

    public static void sendSpawnPacket(Player player, Entity entity){
        sendPacket(player, new PacketPlayOutSpawnEntityLiving(((CraftLivingEntity) entity).getHandle()));
    }

    private static void sendPacket(Player player, Packet<?> packet){
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

}