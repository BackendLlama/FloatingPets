package net.llamasoftware.spigot.floatingpets.menu;

import net.llamasoftware.spigot.floatingpets.Constants;
import net.llamasoftware.spigot.floatingpets.FloatingPets;
import net.llamasoftware.spigot.floatingpets.api.model.Pet;
import net.llamasoftware.spigot.floatingpets.api.model.Setting;
import net.llamasoftware.spigot.floatingpets.api.model.Skill;
import net.llamasoftware.spigot.floatingpets.locale.Locale;
import net.llamasoftware.spigot.floatingpets.manager.storage.StorageManager;
import net.llamasoftware.spigot.floatingpets.menu.model.Menu;
import net.llamasoftware.spigot.floatingpets.menu.model.MenuItem;
import net.llamasoftware.spigot.floatingpets.menu.model.MenuItemRepository;
import net.llamasoftware.spigot.floatingpets.model.misc.SkillCategory;
import net.llamasoftware.spigot.floatingpets.model.misc.SkillLevel;
import net.llamasoftware.spigot.floatingpets.util.ItemBuilder;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MenuSkillCategoryList extends Menu {

    public MenuSkillCategoryList(String title,
                                 Pet pet,
                                 List<SkillCategory> categories) {

        super(title, 1);
        setData("pet", pet);
        setData("categories", categories);
    }

    @SuppressWarnings("unchecked")
    @Override
    public MenuItemRepository getItems() {

        List<SkillCategory> categories = (List<SkillCategory>) getData("categories");
        final FloatingPets plugin = getPlugin();
        Pet pet = getData("pet", Pet.class);
        MenuItemRepository repository = new MenuItemRepository();

        for (int i = 0; i < categories.size(); i++) {
            SkillCategory category = categories.get(i);
            Optional<Skill> skill = pet.getSkillOfType(category.getType());
            boolean hasSkill = skill.isPresent();
            final String skillName = plugin.getLocale()
                    .getText("skills." + category.getType().name().toLowerCase());

            int skillLevel = hasSkill ? skill.get().getLevel() : 0;

            List<String> lore = plugin.getStorageManager().getLocaleListByKey("menus.skill-list.lore" + (hasSkill ? "-bought":""));
            lore = lore.stream().map(l -> {
                String s =
                        l.replace("%price%", String.valueOf(category.getLevels()
                                .get(hasSkill ? (skillLevel == category.getLevels().size()
                                ? category.getLevels().size() - 1 : skillLevel) : 0).getCost()))
                        .replace("%currency_symbol%", plugin.getStringSetting(Setting.PET_SHOP_FORMAT_CURRENCY));
                if(hasSkill) {
                    s = s.replace("%level%", String.valueOf(skill.get().getLevel()));
                }

                return s;
            }).collect(Collectors.toList());

            repository.add(new MenuItem(new ItemBuilder(category.getDisplayItem())
                    .name(plugin.getLocale().getText("menus.skill-list.item",
                            new Locale.Placeholder("skill",
                                    plugin.getLocale().getText("skills." + category.getType().name().toLowerCase()))) +
                            (skillLevel == category.getLevels().size()
                                    ? plugin.getLocale().getText("menus.skill-list.max-level") : ""))

                    .lore(lore.toArray(new String[0]))
                    .build(), i) {

                @Override
                public void onClick(Player player) {

                    if(!plugin.isEconomy()) {
                        player.sendMessage(ChatColor.RED + "This feature is disabled because no economy plugin is present.");
                        return;
                    }

                    SkillLevel level;

                    if(!skill.isPresent()) {
                        level = category.getLevels().get(0);
                    } else {
                        int nextLevel = skill.get().getLevel();
                        if(nextLevel >= category.getLevels().size()){
                            player.sendMessage(ChatColor.RED + "This skill is maxed out!");
                            return;
                        }

                        level = category.getLevels().get(nextLevel);
                    }

                    Economy economy = plugin.getEconomy();
                    double cost     = level.getCost();

                    if(economy.getBalance(player) < cost){
                        plugin.getLocale().send(player, "shop.no_afford", false);
                        return;
                    }

                    economy.withdrawPlayer(player, cost);

                    if(hasSkill){
                        pet.getSkills().remove(skill.get());
                    }

                    Skill newSkill = category.getLevels().get(level.getLevel() - 1).getSkill();

                    if(newSkill.getType() == Skill.Type.STORAGE && !hasSkill){
                        pet.setExtra("storage", new ArrayList<>());
                    }

                    pet.getSkills().add(newSkill);
                    newSkill.applySkill(pet);
                    plugin.getStorageManager().updatePet(pet, StorageManager.Action.SKILL);

                    if(pet.getExtra() != null) {
                        plugin.getStorageManager().updatePet(pet, StorageManager.Action.EXTRA);
                    }

                    economy.depositPlayer(player, cost);
                    plugin.getLocale().send(player, "skill.bought", true,
                            new Locale.Placeholder("skill", skillName),
                            new Locale.Placeholder("level", String.valueOf(level.getLevel())),
                            new Locale.Placeholder("price",
                                    Constants.DEFAULT_DECIMAL_FORMAT.format(cost)),
                            new Locale.Placeholder("currency_symbol",
                                    plugin.getStringSetting(Setting.PET_SHOP_FORMAT_CURRENCY)));

                    player.closeInventory();
                }

            });
        }

        return repository;
    }

}
