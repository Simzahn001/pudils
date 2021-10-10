package me.simzahn.pudils.listeners;

import me.simzahn.pudils.Main;
import me.simzahn.pudils.util.Difficulty;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class EntityRegenerateListener implements Listener {

    @EventHandler
    public void onRegenerate(EntityRegainHealthEvent event) {
        if(event.getEntityType().equals(EntityType.PLAYER)) {
            Difficulty currentDifficulty = Difficulty.valueOf(Main.getPlugin().getConfig().getString("difficulty"));
            if(currentDifficulty == Difficulty.HALF_HEART || currentDifficulty == Difficulty.UUHC) {
                event.setCancelled(true);
            }else if(currentDifficulty == Difficulty.UHC) {
                if(event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
