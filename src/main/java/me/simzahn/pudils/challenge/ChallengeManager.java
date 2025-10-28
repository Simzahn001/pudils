package me.simzahn.pudils.challenge;

import me.simzahn.pudils.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChallengeManager {

    private final List<Challenge> registeredChallenges;




    public ChallengeManager() {
        registeredChallenges = new ArrayList<>();
    }


    //register a new challenge
    public void registerChallenge(Challenge challenge) {
        registeredChallenges.add(challenge);

        //check if the challenge is already registered in the database
        final String SELECT = "SELECT ID FROM challenge WHERE name=?";
        try(Connection connection = Main.getPlugin().getHikari().getConnection();
            PreparedStatement stmtSelect = connection.prepareStatement(SELECT)) {

            stmtSelect.setString(1, challenge.getName());

            ResultSet resultSelect = stmtSelect.executeQuery();

            if (!resultSelect.next()) {
                //register the challenge
                final String INSERT = "INSERT INTO challenge (name, displayName, active) VALUES(?, ?, ?);";
                PreparedStatement stmtInsert = connection.prepareStatement(INSERT);
                stmtInsert.setString(1, challenge.getName());
                stmtInsert.setString(2, "none");
                stmtInsert.setBoolean(3, false);
                stmtInsert.execute();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //returns a list with all registered Challenges.
    //to register a new Challenge, use registerChallenge().
    public List<Challenge> getAllRegisteredChallenges() {
        return registeredChallenges;
    }



    //returns null, if no challenge with the name is found
    //returns the challenge with the given name
    public @Nullable Challenge getChallenge(String name) {
        for (Challenge challenge : getAllRegisteredChallenges()) {
            if (name.equals(challenge.getName())) {
                return challenge;
            }
        }
        return null;
    }



    //toggles if the challenges (only the active ones) are executed.
    //Used for: Challenges are only executed if the timer is running.
    public void toggleChallenges(boolean active) {

        //get all active challenges from the db
        try(Connection connection = Main.getPlugin().getHikari().getConnection();
            PreparedStatement stmt = connection.prepareStatement("SELECT name FROM challenge WHERE active=?")) {
            stmt.setBoolean(1, true);
            ResultSet rs = stmt.executeQuery();


            if (active) {
                //start logic


                while (rs.next()) {
                    Challenge challenge = getChallenge(rs.getString("name"));
                    if (challenge == null) {
                        Bukkit.broadcast(Component.text("Challenge " + rs.getString("name") +
                                " wurde nicht als registrierte Challenge gefunden!")
                                .color(TextColor.color(255, 0, 0)).decorate(TextDecoration.BOLD));
                        continue;
                    }

                    challenge.onStart();


                    if (challenge instanceof ListenerChallenge) {

                        //register the listener
                        Listener listener = ((ListenerChallenge) challenge).getChallengeListener();
                        Main.getPlugin().getPluginManager().registerEvents(listener, Main.getPlugin());

                    } else if (challenge instanceof SchedulerChallenge) {

                        //add the schedulers
                        BukkitRunnable x = ((SchedulerChallenge) challenge).getRunnable();
                        int period = Math.round(((SchedulerChallenge) challenge).getPeriod()/20);
                        if (period != 0) {
                            x.runTaskTimer(Main.getPlugin(), period, period);
                        } else {
                            //professional error handling xD
                            Main.getPlugin().getLogger().info("Die Scheduler-Challenge \"" + challenge.getName() +
                                    "\" konnte nicht registriert werden, weil die Periodendauer null ticks beträgt!");
                        }
                    }

                }


            }else {
                //stop logic

                while (rs.next()) {
                    Challenge challenge = getChallenge(rs.getString("name"));
                    if (challenge == null) {
                        Bukkit.broadcast(Component.text("Challenge " + rs.getString("name") +
                                " wurde nicht als registrierte Challenge gefunden!")
                                .color(TextColor.color(255, 0, 0)).decorate(TextDecoration.BOLD));
                        continue;
                    }

                    challenge.onStop();

                    if (challenge instanceof ListenerChallenge) {
                        ListenerChallenge listenerChallenge = (ListenerChallenge) challenge;

                        HandlerList.unregisterAll(listenerChallenge.getChallengeListener());


                    } else if (challenge instanceof SchedulerChallenge) {
                        ((SchedulerChallenge) challenge).getRunnable().cancel();
                    }
                }
            }


        } catch (SQLException e) {
            Bukkit.broadcast(Component.text("Die aktiven Challenges konnten nicht abgefragt werden!")
                    .color(TextColor.color(255, 0, 0)).decorate(TextDecoration.BOLD));
            throw new RuntimeException(e);
        }
    }


    //Returns whether a challenge is enabled or not.
    //Returns null, if the challenge isn't found in the database.
    public @NotNull Optional<Boolean> isChallengeEnabled(@NotNull Challenge challenge) {
        try(Connection connection = Main.getPlugin().getHikari().getConnection();
            PreparedStatement stmt = connection.prepareStatement("SELECT active FROM challenge WHERE name=?")) {

            stmt.setString(1, challenge.getName());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(rs.getBoolean("active"));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    //enable a challenge
    public boolean enableChallenge(Challenge challenge) {
        try (Connection connection = Main.getPlugin().getHikari().getConnection();
            PreparedStatement stmt = connection.prepareStatement("UPDATE challenge SET active=true WHERE name=?")) {

            stmt.setString(1, challenge.getName());
            stmt.execute();

            Bukkit.broadcast(
                    Component.text(challenge.getName())
                        .append(Component.text(" wurde aktiviert!"))
            );

            return true;
        } catch (SQLException e) {
            return false;
        }
    }


    //disable a challenge
    public boolean disableChallenge(Challenge challenge) {
        try (Connection connection = Main.getPlugin().getHikari().getConnection();
             PreparedStatement stmt = connection.prepareStatement("UPDATE challenge SET active=false WHERE name=?")) {

            stmt.setString(1, challenge.getName());
            stmt.execute();

            Bukkit.broadcast(
                    Component.text(challenge.getName())
                            .append(Component.text(" wurde deaktiviert!"))
            );

            return true;
        } catch (SQLException e) {
            return false;
        }
    }


    //toggle a challenge.
    //if the challenge is enabled, it will be disabled and vice versa.
    //returns the new state of the challenge.
    //returns null, if the challenge isn't found in the database.
    public @NotNull Optional<Boolean> toggleChallenge(Challenge challenge) {
        Optional<Boolean> isEnabled = isChallengeEnabled(challenge);
        if (isEnabled.isEmpty()) {
            return Optional.empty();
        }
        if (isEnabled.get()) {
            disableChallenge(challenge);
            return Optional.of(false);
        } else {
            enableChallenge(challenge);
            return Optional.of(true);
        }
    }
}
