package me.simzahn.pudils;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.signature.qual.IdentifierOrPrimitiveType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DamageListener implements Listener {

    private String SELECTchallange = "SELECT name, id FROM challenge WHERE active=?";

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

                        //send messages to the Players
                        for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
                            currentPlayer.sendTitle("§4Ihr habt reingesuckt!", "");
                            currentPlayer.sendMessage("§1--------------------------------------");
                            currentPlayer.sendMessage("");
                            currentPlayer.sendMessage("§6§fIhr habt reingesuckt!");
                            currentPlayer.sendMessage("§6§fDer Spieler §1§f" + player.getName() + " §6§f hat reingschissen!");
                            currentPlayer.sendMessage("§6§fDeath Cause: §1§f" + event.getCause().toString());
                        }

                        try(Connection connection = Main.getPlugin().getHikari().getConnection();
                            PreparedStatement select = connection.prepareStatement(SELECTchallange)) {

                            select.setBoolean(1, true);

                            ResultSet result = select.executeQuery();

                            //@TODO get the amout of times the Players already failed this challenge and display it

                            Bukkit.getOnlinePlayers().forEach(p -> {p.sendMessage(""); player.sendMessage("§6§fFolgende Challenges waren aktiv:");});
                            while (result.next()) {
                                for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
                                    currentPlayer.sendMessage(" §1- §6§f" + result.getString("name"));
                                }
                            }
                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }


                        for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
                            currentPlayer.sendMessage("");
                            currentPlayer.sendMessage("§6§1");
                            currentPlayer.sendMessage("");
                            currentPlayer.sendMessage("§1--------------------------------------");


                            //the the Player's gamemodes to spectator
                            currentPlayer.setGameMode(GameMode.SPECTATOR);
                        }



                        /*
                        Log the Try to the Database
                         */

                        try (Connection connection = Main.getPlugin().getHikari().getConnection();
                            PreparedStatement insertAttempts = connection.prepareStatement("INSERT INTO attemps (time) VALUES (?); SELECT LAST_INSERT_ID() AS lastID");
                            PreparedStatement selectPlayers = connection.prepareStatement("SELECT ID FROM player WHERE playing=?");
                            PreparedStatement insertAttemptsPlayer = connection.prepareStatement("INSERT INTO attempsPlayer VALUES (?, ?)");
                            PreparedStatement selectChallenge = connection.prepareStatement("SELECT ID FROM challenge WHERE active=1");
                            PreparedStatement insertAttemptsChallenge = connection.prepareStatement("INSERT INTO attempsChallenge VALUES (?, ?)")) {


                            //Log the time and get the attempt ID
                            int attemptID;
                            insertAttempts.setInt(1, Main.getTimer().getSeconds());
                            ResultSet resultAttempts = insertAttempts.executeQuery();

                            if (resultAttempts.next()) {
                                attemptID = resultAttempts.getInt("lastID");

                                //log the playing Players
                                selectPlayers.setBoolean(1, true);
                                ResultSet resultPlayer = selectPlayers.executeQuery();

                                while(resultPlayer.next()) {
                                    insertAttemptsPlayer.setInt(1, attemptID);
                                    insertAttemptsPlayer.setInt(2, resultPlayer.getInt("ID"));
                                }

                                //log the active challenges
                                selectChallenge.setBoolean(1, true);
                                ResultSet resultChallenge = selectChallenge.executeQuery();

                                while (resultChallenge.next()) {
                                    insertAttemptsChallenge.setInt(1, attemptID);
                                    insertAttemptsChallenge.setInt(2, resultPlayer.getInt("ID"));
                                }

                            }else {
                                Bukkit.broadcast(Component.text("§4Etwas ist beim Loggen des Trys schiefgelaufen!"));
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
