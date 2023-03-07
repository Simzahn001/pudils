package me.simzahn.pudils.listeners;

import me.simzahn.pudils.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JoinListener implements Listener {

    private final String SELECT = "SELECT * FROM player WHERE uuid=?";
    private final String INSERT = "INSERT INTO player(uuid, name, playing) VALUES(?,?,?)";

    @EventHandler(ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {

        new BukkitRunnable() {
            @Override
            public void run() {

                //insert Player login
                try(Connection connection = Main.getPlugin().getHikari().getConnection();
                    PreparedStatement insertLogin = connection.prepareStatement("""
                        INSERT INTO playerJoinLeave(playerID, time, online) 
                        SELECT (
                            player.ID,
                            CURRENT_TIMESTAMP,
                            ?
                        ) FROM player WHERE uuid = ?;
                    """)) {
                    insertLogin.setBoolean(1, true);
                    insertLogin.setString(2, event.getPlayer().getUniqueId().toString());
                    insertLogin.execute();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }


                //check if the player is registered in the database
                //if not, add him
                try(Connection connection = Main.getPlugin().getHikari().getConnection();
                    PreparedStatement select = connection.prepareStatement(SELECT)) {

                    select.setString(1, event.getPlayer().getUniqueId().toString());

                    ResultSet result = select.executeQuery();
                    if (!result.next()) {
                        PreparedStatement insert = connection.prepareStatement(INSERT);
                        insert.setString(1, event.getPlayer().getUniqueId().toString());
                        insert.setString(2, event.getPlayer().getName());
                        insert.setBoolean(3, false);
                        insert.execute();
                        event.getPlayer().kick(
                                Component.text("Du wirst gerade in unseren Datenbanken registriert! "
                                + "Versuche es in 10s erneut. Diese Nachricht sollte nur beim 1. Mal Joinen auftreten. "
                                + "Sollte dies nicht so sein, kontaktiere bitte @Simzahn")
                                        .color(TextColor.color(255, 0, 0))
                        );
                    }else {
                        if (!result.getBoolean("playing")) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    event.getPlayer().setGameMode(GameMode.SPECTATOR);
                                }
                            }.runTaskLater(Main.getPlugin(), 1);
                            event.getPlayer().showTitle(Title.title(
                                    Component.text("Du bist ")
                                            .color(TextColor.color(255, 255, 255))
                                        .append(Component.text("Zuschauer!")
                                            .color(TextColor.color(255, 177, 68)).decorate(TextDecoration.BOLD)),
                                    Component.text("Frage einen Spieler, ob du mitmachen darfst!")
                                            .color(TextColor.color(255, 255, 255))
                            ));
                        }else {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    event.getPlayer().setGameMode(GameMode.SURVIVAL);
                                }
                            }.runTaskLater(Main.getPlugin(), 1);
                        }
                    }

                } catch (SQLException exception) {
                    exception.printStackTrace();
                }


            }
        }.runTaskAsynchronously(Main.getPlugin());


    }

}
