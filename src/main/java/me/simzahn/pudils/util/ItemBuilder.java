package me.simzahn.pudils.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ItemBuilder {


    private ItemStack itemStack;
    private ItemMeta itemMeta;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();

    }

    public ItemBuilder setDisplayname (String name) {
        this.itemMeta.setDisplayName(name);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        itemMeta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder addLore(String lore) {
        if(itemMeta.hasLore()) {
            List<String> newLore = itemMeta.getLore();
            newLore.add(lore);
            itemMeta.setLore(newLore);
        }else {
            itemMeta.setLore(Collections.singletonList(lore));
        }
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemStack save() {
        this.itemStack.setItemMeta(this.itemMeta);
        return this.itemStack;
    }

}
