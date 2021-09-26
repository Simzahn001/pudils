package me.simzahn.pudils.listeners;

import me.simzahn.pudils.Main;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JoinEvent implements Listener {

    private String SELECT = "SELECT * FROM player WHERE uuid=?";
    private String INSERT = "INSERT INTO player(uuid, name, playing) VALUES(?,?,?)";

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        new BukkitRunnable() {
            @Override
            public void run() {

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
                        event.getPlayer().kick(Component.text("§4Du wirst gerade in unseren Datenbanken registriert! Versuche es in 10s erneut. Diese Nachricht sollte nur beim 1. Mal Joinen auftreten. Sollte dies nicht so sein, kontaktiere bitte @Simzahn"));
                    }else {
                        if (!result.getBoolean("playing")) {
                            event.getPlayer().setGameMode(GameMode.SPECTATOR);
                            event.getPlayer().sendTitle("Du bist §6§fZuschauer", "Frage einen Spielenden, ob du mitmachen darfst!", 10, 5*20, 10);
                        }else {
                            event.getPlayer().setGameMode(GameMode.SURVIVAL);
                        }
                    }

                } catch (SQLException exception) {
                    exception.printStackTrace();
                }


            }
        }.runTaskAsynchronously(Main.getPlugin());


    }

}
