package me.simzahn.pudils.death;

import me.simzahn.pudils.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class FinishedTry {

    // A variable that stores the duration of the try.
    private final int duration;
    // It stores the player that failed the try.
    private final Player failedPlayer;
    // It stores whether the try was successful or not.
    private final boolean successful;
    // A variable that stores whether the player has already been to the nether.
    private boolean nether = false;
    // A variable that stores whether the player has already been to the end.
    private boolean end = false;


    // Stops the timer and sets all Players to gamemode SPECTATOR.
    // Look at #log() and #informPlayers() to do the remaining stuff
    public FinishedTry(boolean successful, Player failedPlayer) {
        this.duration = Main.getTimer().getSeconds();
        this.failedPlayer = failedPlayer;
        this.successful = successful;

        //set game-mode to spectator
        Bukkit.getOnlinePlayers().forEach(player -> player.setGameMode(GameMode.SPECTATOR));

        Main.getTimer().stop();
    }

    // Logging the try into the database.
    public boolean log() {

        try (Connection connection = Main.getPlugin().getHikari().getConnection();

             PreparedStatement insertAttempt = connection.prepareStatement("""
                INSERT INTO attempt(duration, nether, end, successful, failedPlayerID)
                VALUES (
                        ?,
                        ?,
                        ?,
                        ?,
                        (SELECT ID FROM player where uuid=?)
                       )
                RETURNING attempt.ID;
             """);

             PreparedStatement insertActiveChallenges = connection.prepareStatement("""
                INSERT INTO attemptChallenge(attemptID, challengeID)
                    SELECT ?, challenge.ID
                    FROM challenge
                    WHERE active = true;
             """);

             PreparedStatement insertPlayers = connection.prepareStatement("""
                INSERT INTO attemptPlayer(attemptID, playerID)
                    SELECT ?,player.ID
                    FROM player
                        LEFT Join playerOnline pOnline ON player.ID = pOnline.playerID
                    WHERE pOnline.online=true AND player.playing=true;
             """)
        ) {

            insertAttempt.setInt(1, this.duration);
            insertAttempt.setBoolean(2, this.nether);
            insertAttempt.setBoolean(3, this.end);
            insertAttempt.setBoolean(4, this.successful);
            insertAttempt.setString(5, this.failedPlayer.getUniqueId().toString());
            ResultSet attemptResult = insertAttempt.executeQuery();

            if (!attemptResult.next()) {
                return false;
            }

            int attemptId = attemptResult.getInt("ID");

            insertActiveChallenges.setInt(1, attemptId);
            insertActiveChallenges.execute();

            insertPlayers.setInt(1, attemptId);
            insertPlayers.execute();

            return true;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    // A method that informs the players about the failed try.
    public void informPlayers() {

        // Retrieving the information about the active challenges from the database.
        ArrayList<String> activeChallenges = new ArrayList<>();
        try(Connection connection = Main.getPlugin().getHikari().getConnection();
            PreparedStatement stmt = connection.prepareStatement("SELECT name, id FROM challenge WHERE active=?")) {

            stmt.setBoolean(1, true);

            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                activeChallenges.add(result.getString("name"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        //send the player the messages
        Bukkit.getOnlinePlayers().forEach(player -> {
            //send a title
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
                            .append(Component.text(failedPlayer.getName())
                                    .color(TextColor.color(0, 36, 254))
                                    .decorate(TextDecoration.BOLD))
                            .append(Component.text(" hat reingeschissen!")
                                    .color(TextColor.color(255, 177, 68))
                                    .decorate(TextDecoration.BOLD))
            );

            player.sendMessage("");
            player.sendMessage(
                    Component.text("Folgende Challenges waren aktiv:")
                            .color(TextColor.color(255, 177, 68))
                            .decorate(TextDecoration.BOLD)
            );

            activeChallenges.forEach(challenge -> player.sendMessage(
                    Component.text(" - ")
                            .color(TextColor.color(0, 36, 254))
                        .append(Component.text(challenge)
                            .color(TextColor.color(255, 177, 68))
                            .decorate(TextDecoration.BOLD))
            ));

            //@TODO get the amount of times the Players already failed this challenge and display it

            player.sendMessage("");
            player.sendMessage(
                    Component.text("--------------------------------------")
                            .color(TextColor.color(0, 36, 254))
            );


        });


    }


    // Call, if the nether has been visited.
    public void netherDone() {
        this.nether = true;
    }

    // Call, if the end has been visited.
    public void endDone() {
        this.end = true;
    }
}
