package gq.zunarmc.spigot.floatingpets.listener;

import gq.zunarmc.spigot.floatingpets.Constants;
import gq.zunarmc.spigot.floatingpets.FloatingPets;
import gq.zunarmc.spigot.floatingpets.api.model.Setting;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PotionListener implements Listener {

    private final FloatingPets plugin;

    public PotionListener(FloatingPets plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event){
        ThrownPotion potion = event.getPotion();
        List<PotionEffect> effects = new ArrayList<>(potion.getEffects());
        List<LivingEntity> affected = event.getAffectedEntities().stream()
                .filter(livingEntity -> livingEntity.hasMetadata(Constants.METADATA_PET)).collect(Collectors.toList());

        event.getAffectedEntities().removeIf(livingEntity -> livingEntity.hasMetadata(Constants.METADATA_PET)
                || livingEntity.hasMetadata(Constants.METADATA_NAME_TAG));

        Optional<LivingEntity> petEntity = event.getAffectedEntities().stream()
                .filter(livingEntity -> livingEntity.hasMetadata(Constants.METADATA_PET)).findAny();

        petEntity.ifPresent(livingEntity -> event.setIntensity(livingEntity, 0));

        if(!plugin.isSetting(Setting.PET_HEALTH)){
            return;
        }

        affected.forEach(entity -> Arrays.stream(new PotionEffect[]{
                plugin.getUtility().switchEffectList(effects, PotionEffectType.HARM, PotionEffectType.HEAL),
                plugin.getUtility().switchEffectList(effects, PotionEffectType.HEAL, PotionEffectType.HARM)
        }).forEach(effect -> effect.apply(entity)));

    }

    @EventHandler
    public void onLingeringPotionSplash(AreaEffectCloudApplyEvent event){

        AreaEffectCloud cloud = event.getEntity();
        List<PotionEffect> effects = new ArrayList<>(cloud.getCustomEffects());

        List<LivingEntity> affected = event.getAffectedEntities().stream()
                .filter(livingEntity -> livingEntity.hasMetadata(Constants.METADATA_PET)).collect(Collectors.toList());

        event.getAffectedEntities().removeIf(livingEntity -> livingEntity.hasMetadata(Constants.METADATA_PET)
                || livingEntity.hasMetadata(Constants.METADATA_NAME_TAG));

        Optional<LivingEntity> petEntity = event.getAffectedEntities().stream()
                .filter(livingEntity -> livingEntity.hasMetadata(Constants.METADATA_PET)).findAny();

        petEntity.ifPresent(livingEntity -> cloud.clearCustomEffects());

        if(!plugin.isSetting(Setting.PET_HEALTH)){
            return;
        }

        affected.forEach(entity -> Arrays.stream(new PotionEffect[]{
                plugin.getUtility().switchEffectList(effects, PotionEffectType.HARM, PotionEffectType.HEAL),
                plugin.getUtility().switchEffectList(effects, PotionEffectType.HEAL, PotionEffectType.HARM)
        }).forEach(effect -> effect.apply(entity)));
    }

}
