package gq.zunarmc.spigot.floatingpets.model.skill;

import gq.zunarmc.spigot.floatingpets.api.model.Pet;
import gq.zunarmc.spigot.floatingpets.api.model.Skill;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.stream.Collectors;

public class BeaconSkill extends Skill {

    private List<PotionEffect> effects;

    public BeaconSkill(Type type, int level) {
        super(type, level);
    }

    @SuppressWarnings("unchecked") @Override
    public void parse(Object object) {
        List<String> stringList = (List<String>) object;
        effects = stringList
                .stream()
                .map(this::deserializeEffect)
                .collect(Collectors.toList());
    }

    private PotionEffect deserializeEffect(String str){
        String[] data = str.split(":");

        PotionEffectType type = PotionEffectType.getByName(data[0]);
        if(type == null)
            return null;

        int amplifier         = Integer.parseInt(data[1]);

        return new PotionEffect(type, 20, amplifier);
    }

    @Override
    public void applySkill(Pet pet) {
        Player player = pet.getOnlineOwner();

        if(player.getLocation().distance(pet.getLocation()) <= 3){
            effects.forEach(player::addPotionEffect);
        }
    }

}