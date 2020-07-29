package net.llamasoftware.spigot.floatingpets.menu;

import net.llamasoftware.spigot.floatingpets.util.ItemBuilder;
import net.llamasoftware.spigot.floatingpets.menu.model.Menu;
import net.llamasoftware.spigot.floatingpets.menu.model.MenuItem;
import net.llamasoftware.spigot.floatingpets.menu.model.MenuItemRepository;
import net.llamasoftware.spigot.floatingpets.api.model.Setting;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class MenuPetParticleCustomize extends Menu {

    public MenuPetParticleCustomize(String title, Particle particle) {
        super(title, 1);
        setData("particle", particle);
    }

    @Override
    public MenuItemRepository getItems() {

        MenuItemRepository repo = new MenuItemRepository();
        addSpeedItem(repo, "slow", 1);
        addSpeedItem(repo, "normal", 3);
        addSpeedItem(repo, "fast", 5);
        addSpeedItem(repo, "fastest", 7);

        return repo;
    }

    public void addSpeedItem(MenuItemRepository repository, String speed, int slot){
        Particle particle = getData("particle", Particle.class);
        int index = getData("index", Integer.class);

        repository.add(new MenuItem(new ItemBuilder(Material.FEATHER)
                .name(getPlugin().getLocale().getText("menus.particle-customization.speed-" + speed)).build(), slot) {
            @Override
            public void onClick(Player player) {
                applyParticle(player, particle,
                        Integer.parseInt(getPlugin().getStringSetting(
                                Setting.valueOf("PET_PARTICLE_SPEED_" + speed.toUpperCase()))), index);
            }
        });
    }

    public static void applyParticle(Player player, Particle particle, int speed, int index){
        Bukkit.dispatchCommand(player, "pet particle " + index + " " + particle.name() + " " + speed);
        player.closeInventory();
    }

}