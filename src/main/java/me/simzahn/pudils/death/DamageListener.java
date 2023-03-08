package me.simzahn.pudils.death;

import me.simzahn.pudils.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class DamageListener implements Listener {


    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {

        // Checking if the player is taking damage and if the player is going to die. If the player is going to die, it
        // cancels the event and logs the try.
        if (event.getEntity().getType().equals(EntityType.PLAYER)) {
            Player player = (Player) event.getEntity();

            if (player.getHealth()-event.getFinalDamage() <= 0) {

                event.setCancelled(true);

                FinishedTry finishedTry = new FinishedTry( false, player);

                //database calls are done, have to do it async
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        finishedTry.informPlayers();
                        if(finishedTry.log()) {
                            Bukkit.broadcast(
                                    Component.text("Beim Loggen des Trys ist etwas schiefgelaufen!")
                                            .color(TextColor.color(255, 0, 0))
                                            .decorate(TextDecoration.BOLD)
                            );
                        }
                    }
                }.runTaskAsynchronously(Main.getPlugin());

            }
        }

    }

}
