package me.simzahn.pudils.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

    @EventHandler()
    public void onInventoryClick(InventoryClickEvent event) {

        if (! (event.getInventory().getHolder() instanceof InventoryGUI)) {
            return;
        }

        InventoryGUI inventoryGUI = (InventoryGUI) event.getInventory().getHolder();
        inventoryGUI.handleClick(event);

    }

}
