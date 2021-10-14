package org.kitpvp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Menu {
    Inventory inventory;

    public Menu(Inventory inventory) {
        this.inventory = inventory;
    }

    public void setSlotMenu(String material, String name, int size, ArrayList lore){
        ItemStack i = new ItemStack(Material.valueOf(material));
        ItemMeta iMeta = i.getItemMeta();
        iMeta.setDisplayName(name);
        iMeta.setLore(lore);
        i.setItemMeta(iMeta);
        inventory.setItem(size, i);

    }

    public void openInventory(Player player){
        player.openInventory(inventory);
    }
}
