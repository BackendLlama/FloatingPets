package net.llamasoftware.spigot.floatingpets.external.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.llamasoftware.spigot.floatingpets.FloatingPets;
import net.llamasoftware.spigot.floatingpets.api.model.Pet;
import net.llamasoftware.spigot.floatingpets.api.model.Setting;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Optional;

public class SteerPacketListener {

    private final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
    private final FloatingPets plugin;

    public SteerPacketListener(FloatingPets plugin){
        this.plugin = plugin;
    }

    public void listen(){

        if(!plugin.isSetting(Setting.PET_RIDING))
            return;

        FloatingPets fpPlugin = plugin;
        protocolManager.addPacketListener(new PacketAdapter(fpPlugin, ListenerPriority.NORMAL, PacketType.Play.Client.STEER_VEHICLE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {

                Player player = event.getPlayer();
                Entity vehicle = player.getVehicle();

                if(vehicle == null)
                    return;

                Optional<Pet> pet = fpPlugin.getPetManager().getPetByEntity(vehicle, true);
                if(!pet.isPresent())
                    return;

                if(pet.get().getEntity() == null
                        || pet.get().getEntity().getEntity() == null)
                    return;

                LivingEntity entity = pet.get().getEntity().getEntity();
                Direction direction = new Direction(event.getPacket());

                if(direction.isMoving()){
                    float yaw = entity.getLocation().getYaw();
                    float pitch = entity.getLocation().getPitch();

                    if(fpPlugin.isSetting(Setting.PET_RIDING_PLAYER_ROTATION)){
                        yaw = player.getLocation().getYaw();
                        pitch = fpPlugin.isSetting(Setting.PET_RIDING_ALLOW_FLY) ? player.getLocation().getPitch() : 0;
                    } else {
                        if(direction.isRight()){
                            yaw += 20;
                        } else if(direction.isLeft()){
                            yaw -= 20;
                        }

                        if(direction.isUpward())
                            pitch -= 15;
                    }

                    entity.setRotation(yaw, pitch);

                    if(direction.isForward()) {
                        entity.setVelocity(entity.getEyeLocation().getDirection().multiply(0.5));
                    }

                }
            }
        });
    }

    private static class Direction {

        private final float forward;
        private final float side;
        private final boolean upward;

        Direction(PacketContainer packet){
            forward = packet.getFloat().read(1);
            side    = packet.getFloat().read(0);
            upward  = packet.getBooleans().read(0);
        }

        public boolean isForward(){ return forward > 0; }

        public boolean isRight(){ return side < 0; }

        public boolean isLeft(){ return side > 0; }

        public boolean isMoving(){ return !(forward == 0.0 && side == 0.0); }

        public boolean isUpward() {
            return upward;
        }

    }

}