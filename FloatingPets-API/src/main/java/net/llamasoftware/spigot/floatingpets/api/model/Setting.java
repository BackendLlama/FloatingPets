package net.llamasoftware.spigot.floatingpets.api.model;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

public enum Setting {

    GENERAL_STORAGE_TYPE("storage.type"),
    METRICS("metrics.enabled"),

    GENERAL_STORAGE_FLATFILE_PETS("storage.options.flatfile.files.pet"),
    GENERAL_STORAGE_FLATFILE_TYPE("storage.options.flatfile.files.type"),
    GENERAL_STORAGE_FLATFILE_MISC("storage.options.flatfile.files.misc"),

    GENERAL_STORAGE_MYSQL_PREFIX("storage.options.mysql.prefix"),
    GENERAL_STORAGE_MYSQL_SERVER("storage.options.mysql.server"),
    GENERAL_STORAGE_MYSQL_PORT("storage.options.mysql.port"),
    GENERAL_STORAGE_MYSQL_DATABASE("storage.options.mysql.database"),
    GENERAL_STORAGE_MYSQL_USERNAME("storage.options.mysql.username"),
    GENERAL_STORAGE_MYSQL_PASSWORD("storage.options.mysql.password"),
    GENERAL_STORAGE_MYSQL_MAXIMUM_POOLS("storage.options.mysql.max_pools"),

    PET_SPAWN_ON_JOIN("pet.spawn_on_join"),
    PET_HEALTH("pet.health.enabled"),

    PET_DEFAULT_HEALTH("pet.health.options.default_health"),
    PET_MAX_HEALTH("pet.health.options.max_health"),

    MULTIPLE_PETS("pet.multiple_pets.enabled"),

    PET_NAME_DEFAULT_NAME("pet.name.default_name"),
    PET_NAME_CUSTOM("pet.name.custom.enabled"),
    PET_NAME_CUSTOM_COLORS("pet.name.custom.colors"),
    PET_NAME_CUSTOM_MINIMUM_LENGTH("pet.name.custom.min_length"),
    PET_NAME_CUSTOM_MAXIMUM_LENGTH("pet.name.custom.max_length"),
    PET_NAME_FORMAT("pet.name.format"),

    PET_COOLDOWN_SELECT("pet.cooldown.select.enabled"),
    PET_HIGHER("pet.higher_pet"),

    PET_DEATH_MESSAGES("pet.health.options.death_message"),
    PET_RESPAWN_ON_DEATH("pet.health.options.respawn_on_death"),
    PET_REMOVE_ON_DEATH("pet.health.options.remove_on_death"),
    PET_RIDING("pet.riding.enabled"),
    PET_RIDING_PLAYER_ROTATION("pet.riding.options.player_rotation"),
    PET_RIDING_ALLOW_FLY("pet.riding.options.allow_fly"),
    PET_DAMAGE_BY_FALL("pet.health.options.damage.fall"),
    PET_DAMAGE_BY_VOID("pet.health.options.damage.void"),
    PET_DAMAGE_BY_ATTACK("pet.health.options.damage.attack.enabled"),
    PET_DAMAGE_ATTACKED_BY_PLAYER("pet.health.options.damage.attack.options.player_attack"),
    PET_DAMAGE_ATTACKED_BY_OWNER("pet.health.options.damage.attack.options.owner_attack"),
    PET_HEALING("pet.health.options.healing.enabled"),
    PET_HAT_VANILLA_IN_AIR("pet.hat.vanilla.in_air.enabled"),
    PET_HAT_VANILLA_DISTANCE("pet.hat.vanilla.in_air.distance"),
    PET_HAT_VANILLA_SWIMMING("pet.hat.vanilla.swimming.enabled"),
    PET_TELEPORTATION_CALL("pet.teleportation.call.enabled"),
    PET_TELEPORTATION_DISTANCE("pet.teleportation.distance.enabled"),
    PET_TELEPORTATION_DISTANCE_DISTANCE("pet.teleportation.distance.distance"),

    PET_SHOP_ENABLED("pet.shop.enabled"),
    PET_SHOP_FORMAT_TEXT("pet.shop.options.format.text"),
    PET_SHOP_FORMAT_DECIMAL("pet.shop.options.format.decimal"),
    PET_SHOP_FORMAT_CURRENCY("pet.shop.options.format.currency_symbol"),
    PET_SHOP_FORMAT_FREE("pet.shop.options.format.free"),

    PET_HAT_COSMETIC("pet.hat.cosmetic.enabled"),
    PET_LIGHT_COSMETIC("pet.light.cosmetic.enabled"),
    PET_SKILLS("pet.skills.enabled"),

    PET_PARTICLE_FILTER("pet.particle.filter.enabled"),
    PET_PARTICLE("pet.particle.enabled"),

    PET_PARTICLE_SPEED_SLOW("pet.particle.speeds.slow"),
    PET_PARTICLE_SPEED_NORMAL("pet.particle.speeds.normal"),
    PET_PARTICLE_SPEED_FAST("pet.particle.speeds.fast"),
    PET_PARTICLE_SPEED_FASTEST("pet.particle.speeds.fastest"),

    WORLD_FILTER("world_filter.enabled"),
    WORLD_FILTER_RESPAWN("world_filter.respawn"),

    PET_STILL_ANIMATION("pet.movement.still_animation.enabled"),
    PET_STILL_ANIMATION_TYPE("pet.movement.still_animation.type"),

    PET_HIDE_NAME_ON_MOVE("pet.movement.hide_name_on_move"),

    PET_PARTICLE_CUSTOMIZATION("pet.particle.allow_customization"),

    ;

    @Getter
    private final String key;

    Setting(String key){
        this.key = key;
    }

    public static Optional<Setting> getSettingByKey(String key){
        return Arrays.stream(Setting.values())
                .filter(setting -> setting.getKey().equals(key))
                .findAny();
    }

}