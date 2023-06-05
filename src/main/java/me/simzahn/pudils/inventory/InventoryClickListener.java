package me.simzahn.pudils.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

    @EventHandler()
    public void onInventoryClick(InventoryClickEvent event) {

        //check if it is the challenge inventory
        if (! (event.getInventory().getHolder() instanceof ChallengeInventory)) {
            return;
        }

        ChallengeInventory challengeInventory = (ChallengeInventory) event.getInventory().getHolder();
        challengeInventory.handleClick(event);

    }

}
