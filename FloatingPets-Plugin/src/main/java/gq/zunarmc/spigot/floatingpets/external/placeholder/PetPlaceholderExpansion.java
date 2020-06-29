package gq.zunarmc.spigot.floatingpets.external.placeholder;

import gq.zunarmc.spigot.floatingpets.Constants;
import gq.zunarmc.spigot.floatingpets.FloatingPets;
import gq.zunarmc.spigot.floatingpets.api.model.Pet;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.List;

public class PetPlaceholderExpansion extends PlaceholderExpansion {

    private final FloatingPets plugin;
    private final PluginDescriptionFile description;

    public PetPlaceholderExpansion(FloatingPets plugin) {
        this.plugin = plugin;
        this.description = plugin.getDescription();
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public String getIdentifier() {
        return description.getName();
    }

    @Override
    public String getAuthor() {
        return description.getAuthors().get(0);
    }

    @Override
    public String getVersion() {
        return description.getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier){

        if(player == null)
            return "";

        List<Pet> pets = plugin.getStorageManager().getPetsByOwner(player.getUniqueId());
        if(pets.size() != 1)
            return "";

        Pet pet = pets.get(0);

        switch (identifier.toLowerCase()){
            case "pet_name":
                return pet.getName();
            case "pet_type":
                return pet.getType().getName();
            case "pet_health":
                return Constants.DEFAULT_DECIMAL_FORMAT.format(pet.getEntity().getEntityHealth());
            default:
                return "";
        }

    }

}