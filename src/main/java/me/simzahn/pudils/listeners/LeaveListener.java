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
                try (Connection connection = Main.getPlugin().getHikari().getConnection();
                     PreparedStatement insertLogin = connection.prepareStatement("""
                        INSERT INTO playerJoinLeave(playerID, time, online)
                        VALUES (
                            (SELECT ID FROM player WHERE uuid=?),
                            CURRENT_TIMESTAMP,
                            ?
                        );
                    """)) {
                    insertLogin.setString(1, event.getPlayer().getUniqueId().toString());
                    insertLogin.setBoolean(2, false);
                    insertLogin.execute();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }

        }.runTaskAsynchronously(Main.getPlugin());
    }
}
