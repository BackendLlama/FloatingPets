package net.llamasoftware.spigot.floatingpets.command.subcommand;

import net.llamasoftware.spigot.floatingpets.FloatingPets;
import net.llamasoftware.spigot.floatingpets.command.Command;
import net.llamasoftware.spigot.floatingpets.command.CommandInfo;
import net.llamasoftware.spigot.floatingpets.menu.MenuSkillCategoryList;
import net.llamasoftware.spigot.floatingpets.model.misc.SkillCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

@CommandInfo(name = "skill", inGame = true)
public class CommandSkill extends Command {

    public CommandSkill(FloatingPets plugin) {
        super(plugin);
    }

    @Override
    public void onCommand(CommandSender sender, String[] arguments) {

        Player player = (Player) sender;

        List<SkillCategory> categories = plugin.getSettingManager().getSkillCategories().stream()
                .filter(skillCategory -> player.hasPermission("floatingpets.skills." + skillCategory.getType().name().toLowerCase()))
                .collect(Collectors.toList());

        if(categories.isEmpty()){
            locale.send(player, "commands.skill.no-skills", false);
        }

        MenuSkillCategoryList menu = new MenuSkillCategoryList("Skills", pet, categories);
        plugin.getMenuManager().openMenu(player, menu, plugin);

    }

}