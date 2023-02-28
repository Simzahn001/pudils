package me.simzahn.pudils.listeners;

import me.simzahn.pudils.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DamageListener implements Listener {


    private final String SELECTChallenge = "SELECT name, id FROM challenge WHERE active=?";
    @EventHandler
    public void onDamage(EntityDamageEvent event) {

        if (event.getEntity().getType().equals(EntityType.PLAYER)) {
            Player player = (Player) event.getEntity();

            if (player.getHealth()-event.getFinalDamage() <= 0) {

                //cancel to avoid death
                event.setCancelled(true);
                //stop timer
                Main.getTimer().stop();

                new BukkitRunnable() {
                    @Override
                    public void run() {

                        Bukkit.getOnlinePlayers().forEach(player -> {
                            player.showTitle(Title.title(
                                    Component.text("Ihr habt reingesuckt!")
                                            .color(TextColor.color(255, 0, 0)),
                                    Component.empty()
                            ));
                            player.sendMessage(
                                    Component.text("--------------------------------------")
                                            .color(TextColor.color(0, 36, 254))
                            );
                            player.sendMessage("");
                            player.sendMessage(
                                    Component.text("Ihr habt reingesuckt!")
                                            .color(TextColor.color(255, 177, 68))
                                            .decorate(TextDecoration.BOLD)
                            );
                            player.sendMessage(
                                    Component.text("Der Spieler ")
                                            .color(TextColor.color(255, 177, 68))
                                            .decorate(TextDecoration.BOLD)
                                        .append(Component.text(player.getName())
                                            .color(TextColor.color(0, 36, 254))
                                            .decorate(TextDecoration.BOLD))
                                        .append(Component.text(" hat reingeshissen!")
                                            .color(TextColor.color(255, 177, 68))
                                            .decorate(TextDecoration.BOLD))
                            );
                            player.sendMessage(
                                    Component.text("Death Cause: ")
                                            .color(TextColor.color(255, 177, 68))
                                            .decorate(TextDecoration.BOLD)
                                        .append(Component.text(event.getCause().toString())
                                            .color(TextColor.color(0, 36, 254))
                                            .decorate(TextDecoration.BOLD))
                            );
                        });

                        try(Connection connection = Main.getPlugin().getHikari().getConnection();
                            PreparedStatement select = connection.prepareStatement(SELECTChallenge)) {

                            select.setBoolean(1, true);

                            ResultSet result = select.executeQuery();

                            //@TODO get the amount of times the Players already failed this challenge and display it

                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.sendMessage("");
                                player.sendMessage(
                                        Component.text("Folgende Challenges waren aktiv:")
                                                .color(TextColor.color(255, 177, 68))
                                                .decorate(TextDecoration.BOLD)
                                );
                                while (result.next()) {
                                    player.sendMessage(
                                            Component.text(" - ")
                                                    .color(TextColor.color(0, 36, 254))
                                                .append(Component.text(result.getString("name"))
                                                    .color(TextColor.color(255, 177, 68))
                                                    .decorate(TextDecoration.BOLD))
                                    );
                                }
                                result.beforeFirst();
                            }

                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }


                        for (Player currentPlayer : Bukkit.getOnlinePlayers()) {

                            currentPlayer.sendMessage("");
                            currentPlayer.sendMessage(
                                    Component.text("--------------------------------------")
                                            .color(TextColor.color(0, 36, 254))
                            );

                            //the Player's gamemodes to spectator
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    currentPlayer.setGameMode(GameMode.SPECTATOR);
                                }
                            }.runTaskLater(Main.getPlugin(), 1);
                        }





                        /*
                        Log the Try to the Database
                         */


                        try (Connection connection = Main.getPlugin().getHikari().getConnection();
                            PreparedStatement insertAttempts = connection.prepareStatement("INSERT INTO attempt (time, nether, end) VALUES (?, ?, ?)");
                            PreparedStatement selectLastID = connection.prepareStatement("SELECT LAST_INSERT_ID() AS lastID");
                            PreparedStatement selectPlayers = connection.prepareStatement("SELECT ID FROM player WHERE playing=?");
                            PreparedStatement insertAttemptsPlayer = connection.prepareStatement("INSERT INTO attemptPlayer VALUES (?, ?)");
                            PreparedStatement selectChallenge = connection.prepareStatement("SELECT ID FROM challenge WHERE active=?");
                            PreparedStatement insertAttemptsChallenge = connection.prepareStatement("INSERT INTO attemptChallenge VALUES (?, ?)")) {


                            //Log the time
                            int attemptID;
                            insertAttempts.setInt(1, Main.getTimer().getSeconds());
                            //@TODO Log if the Players were in the end or nether
                            insertAttempts.setBoolean(2, false);
                            insertAttempts.setBoolean(3, false);
                            insertAttempts.execute();

                            //get the attempt ID
                            ResultSet resultLastID = selectLastID.executeQuery();

                            if (resultLastID.next()) {
                                attemptID = resultLastID.getInt("lastID");

                                //log the playing Players
                                selectPlayers.setBoolean(1, true);
                                ResultSet resultPlayer = selectPlayers.executeQuery();

                                while(resultPlayer.next()) {
                                    insertAttemptsPlayer.setInt(1, attemptID);
                                    insertAttemptsPlayer.setInt(2, resultPlayer.getInt("ID"));
                                    insertAttemptsPlayer.execute();
                                }

                                //log the active challenges
                                selectChallenge.setBoolean(1, true);
                                ResultSet resultChallenge = selectChallenge.executeQuery();

                                while (resultChallenge.next()) {
                                    insertAttemptsChallenge.setInt(1, attemptID);
                                    insertAttemptsChallenge.setInt(2, resultChallenge.getInt("ID"));
                                    insertAttemptsChallenge.execute();
                                }

                            }else {
                                Bukkit.broadcast(
                                        Component.text("Etwas ist beim Eintragen des Trys in die Datenbank schiefgelaufen!")
                                                .color(TextColor.color(255, 0, 0))
                                );

                            }


                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }



                    }
                }.runTaskAsynchronously(Main.getPlugin());

            }
        }

    }

}
