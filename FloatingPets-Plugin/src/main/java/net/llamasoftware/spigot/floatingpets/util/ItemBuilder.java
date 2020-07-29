package net.llamasoftware.spigot.floatingpets.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

public class ItemBuilder {

    private final ItemStack stack;

    public ItemBuilder(ItemStack stack){
        this.stack = stack;
    }

    public ItemBuilder(Material material){
        this.stack = new ItemStack(material);
    }

    public ItemBuilder(Material material, int amount){
        this.stack = new ItemStack(material, amount);
    }

    public ItemBuilder name(String name){
        ItemMeta meta = stack.getItemMeta();
        if(meta == null)
            return this;

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        stack.setItemMeta(meta);

        return this;
    }

    public ItemBuilder lore(String... lore){
        ItemMeta meta = stack.getItemMeta();
        if(meta == null)
            return this;

        meta.setLore(Arrays.stream(lore)
                .map(l -> ChatColor.translateAlternateColorCodes('&', l))
                .collect(Collectors.toList()));

        stack.setItemMeta(meta);

        return this;
    }

    public ItemBuilder unbreakable(boolean value){
        ItemMeta meta = stack.getItemMeta();
        if(meta == null)
            return this;

        meta.setUnbreakable(value);
        stack.setItemMeta(meta);

        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment, int level){
        stack.addEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder enchantUnsafe(Enchantment enchantment, int level){
        stack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder damage(int damage){
        if(!(stack.getItemMeta() instanceof Damageable))
            return this;

        Damageable damageable = (Damageable) stack.getItemMeta();
        damageable.setDamage(damage);

        return this;
    }

    @Deprecated
    public ItemBuilder skullOwner(String ownerName){
        if(!(stack.getItemMeta() instanceof SkullMeta))
            return this;

        SkullMeta skullMeta = (SkullMeta) stack.getItemMeta();
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(ownerName));

        stack.setItemMeta(skullMeta);
        return this;
    }

    public ItemBuilder skullOwner(UUID uniqueId){
        if(!(stack.getItemMeta() instanceof SkullMeta)) {
            return this;
        }

        SkullMeta skullMeta = (SkullMeta) stack.getItemMeta();
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uniqueId));

        stack.setItemMeta(skullMeta);
        return this;
    }

    @Deprecated
    public ItemBuilder data(MaterialData data){
        stack.setData(data);
        return this;
    }

    public ItemStack build(){
        return stack;
    }

}