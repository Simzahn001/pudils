package me.simzahn.pudils.listeners;

import me.simzahn.pudils.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LeaveListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {

        new BukkitRunnable() {

            @Override
            public void run() {

                //insert Player logoff
                try(Connection connection = Main.getPlugin().getHikari().getConnection();
                    PreparedStatement insertLogin = connection.prepareStatement("""
                        INSERT INTO playerJoinLeave(playerID, time, online) 
                        SELECT (
                            player.ID,
                            CURRENT_TIMESTAMP,
                            ?
                        ) FROM player WHERE uuid = ?;
                    """)) {
                    insertLogin.setBoolean(1, false);
                    insertLogin.setString(2, event.getPlayer().getUniqueId().toString());
                    insertLogin.execute();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }.runTaskAsynchronously(Main.getPlugin());

    }

}

