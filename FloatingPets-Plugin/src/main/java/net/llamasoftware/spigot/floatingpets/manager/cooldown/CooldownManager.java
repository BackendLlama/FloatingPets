package net.llamasoftware.spigot.floatingpets.manager.cooldown;

import net.llamasoftware.spigot.floatingpets.model.misc.Cooldown;

import java.util.*;

public class CooldownManager {

    private final Map<UUID, List<Cooldown>> cooldowns = new HashMap<>();

    public Optional<Cooldown> getCooldown(UUID uniqueId, Cooldown.Type type){
        cooldowns.forEach((key, value) -> value.removeIf(Cooldown::isExpired));

        if(!cooldowns.containsKey(uniqueId))
            return Optional.empty();

        return cooldowns.get(uniqueId)
                .stream()
                .filter(cooldown -> cooldown.getType() == type)
                .findAny();
    }

    public void addCooldown(UUID uniqueId, Cooldown.Type type, long expiry) {
        if(!cooldowns.containsKey(uniqueId))
            cooldowns.put(uniqueId, new ArrayList<>());

        List<Cooldown> list = cooldowns.get(uniqueId);
        list.add(Cooldown.builder().type(type).expiry(expiry).build());
    }

}