package me.simzahn.pudils.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public interface InventoryGUI extends InventoryHolder {

    void handleClick(InventoryClickEvent event);

}
