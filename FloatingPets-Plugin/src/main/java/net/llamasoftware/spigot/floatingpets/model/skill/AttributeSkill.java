package net.llamasoftware.spigot.floatingpets.model.skill;

import net.llamasoftware.spigot.floatingpets.api.model.Pet;
import net.llamasoftware.spigot.floatingpets.api.model.Skill;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;

public class AttributeSkill extends Skill {

    private Attribute attribute;
    private double baseValue;

    public AttributeSkill(Type type, int level) {
        super(type, level);
    }

    @Override
    public void parse(Object object) {
        baseValue = (double) object;
        attribute = Attribute.valueOf(type.name());
    }

    @Override
    public void applySkill(Pet pet) {
        AttributeInstance attribute = pet.getEntity().getEntity().getAttribute(this.attribute);
        if (attribute != null) {
            attribute.setBaseValue(baseValue);
        }
    }

}