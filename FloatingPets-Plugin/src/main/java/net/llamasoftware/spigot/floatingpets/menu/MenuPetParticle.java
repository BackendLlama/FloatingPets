package net.llamasoftware.spigot.floatingpets.menu;

import net.llamasoftware.spigot.floatingpets.util.ItemBuilder;
import net.llamasoftware.spigot.floatingpets.FloatingPets;
import net.llamasoftware.spigot.floatingpets.api.model.Pet;
import net.llamasoftware.spigot.floatingpets.api.model.Setting;
import net.llamasoftware.spigot.floatingpets.locale.Locale;
import net.llamasoftware.spigot.floatingpets.model.misc.ParticleInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MenuPetParticle extends ListMenu<ParticleInfo> {

    public MenuPetParticle(String title, List<ParticleInfo> display) {
        super(title, 6);
        setData("menuIndex", 0);
        setData("list", display);
    }

    @Override
    public ItemStack buildItem(ParticleInfo particle) {
        String itemName = getPlugin().getLocale().getText("menus.particle.item",
                new Locale.Placeholder("particle", particle.getParticle().name()));

        return new ItemBuilder(particle.getMaterial()).name(itemName).build();
    }

    @Override
    public void onClick(Player player, ParticleInfo particle, int index) {
        FloatingPets plugin = getPlugin();
        Pet pet = getData("pet", Pet.class);
        int pIndex = getData("index", Integer.class);

        if (pet.hasParticle()) {
            plugin.getLocale().send(player, "commands.particle.removed-current", false);
            pet.setParticle(null);
        }

        if (plugin.isSetting(Setting.PET_PARTICLE_CUSTOMIZATION) && player.hasPermission("floatingpets.particle.customization")) {
            MenuPetParticleCustomize menu = new MenuPetParticleCustomize(plugin.getStorageManager()
                    .getLocaleByKey("menus.particle-customization.title"), particle.getParticle());
            menu.setData("index", pIndex);
            getMenuManager().openMenu(player, menu, getPlugin());
        } else {
            Bukkit.dispatchCommand(player, "pet particle " + pIndex + " " + particle.getParticle().name() + " 20");
        }
    }

}