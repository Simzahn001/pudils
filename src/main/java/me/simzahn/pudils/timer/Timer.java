package me.simzahn.pudils.timer;

import me.simzahn.pudils.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Timer {

    private int seconds;
    private boolean isRunning = false;
    private BukkitTask runnable;
    //a list of all active players
    //the list is reloaded every time the timer resumes
    private List<Player> activePlayers = new ArrayList<>();


    public Timer() {
        this.seconds = Main.getPlugin().getConfig().getInt("timer.seconds");
    }

    public boolean start() {
        boolean successful = reset();
        if (!successful) {
            return false;
        }
        successful = resume();
        return successful;
    }
    public boolean start(boolean messagePlayers) {
        boolean successful = start();
        if (successful) {
            if (messagePlayers) {
                Bukkit.broadcast(
                        Component.text("Der Timer wurde erfolgreich ")
                                .color(TextColor.color(255, 255, 255))
                            .append(Component.text("resetted")
                                .color(TextColor.color(0, 36, 254))
                                .decorate(TextDecoration.BOLD))
                            .append(Component.text(" und ")
                                .color(TextColor.color(255,255,255)))
                            .append(Component.text("gestartet")
                                .color(TextColor.color(33, 255, 0))
                                .decorate(TextDecoration.BOLD))
                            .append(Component.text("!")
                                .color(TextColor.color(255, 255, 255)))
                );
            }
            return true;
        }
        if (messagePlayers) {
            Bukkit.broadcast(
                Component.text("Der Timer konnte ")
                        .color(TextColor.color(255,255,255))
                    .append(Component.text("nicht resetted und gestartet")
                        .color(TextColor.color(122, 0, 3))
                        .decorate(TextDecoration.BOLD))
                    .append(Component.text(" werden!")
                        .color(TextColor.color(255,255,255)))
            );
        }

        return false;
    }

    //Starts the timer from the current timestamp (not from zero).
    //Return false, if the timer is already running and true if the timer was resumed successfully.
    public boolean resume() {
        if(this.isRunning) {
            return false;
        }
        isRunning = true;
        this.runnable = new BukkitRunnable() {
            @Override
            public void run() {
                //just for safety
                if(!isRunning) {
                    stop();
                }

                Bukkit.getOnlinePlayers().forEach(player ->
                    player.sendActionBar(
                            Component.text((
                /*hours   -->*/     seconds/60/60<10 ? "0"+seconds/60/60 : seconds /60/60) + ":"
                /*minutes -->*/     + (seconds/60%60<10?"0"+seconds/60%60:seconds/60%60) + ":"
                /*seconds -->*/     + (seconds%60<10?"0"+seconds%60:seconds%60))
                                    .color(TextColor.color(255, 177, 68))
                                    .decorate(TextDecoration.BOLD)
                    )
                );
                seconds++;
            }
        }.runTaskTimer(Main.getPlugin(), 20, 20);

        //reload list of active Players

            try (Connection connection = Main.getPlugin().getHikari().getConnection();
                 PreparedStatement stmt = connection.prepareStatement("SELECT uuid FROM player WHERE playing=?")) {

                stmt.setBoolean(1, true);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    System.out.println(rs.getString("uuid"));
                    Player player = Bukkit.getPlayer(UUID.fromString(rs.getString("uuid")));
                    if (player != null) {
                        activePlayers.add(player);
                    }
                }
                System.out.println("Active Players: " + activePlayers.size());


            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        Main.getChallengeManager().toggleChallenges(true);

        return true;
    }
    public boolean resume(boolean messagePlayers) {
        boolean successful = this.resume();
        if (successful) {
            //message players if the boolean is true
            if (messagePlayers) {
                Bukkit.broadcast(
                    Component.text("Der Timer wurde erfolgreich ")
                            .color(TextColor.color(255, 255, 255))
                        .append(Component.text("gestartet")
                            .color(TextColor.color(33, 255, 0))
                            .decorate(TextDecoration.BOLD))
                        .append(Component.text("!")
                            .color(TextColor.color(255, 255, 255)))
                );
            }

            return true;
        }
        if (messagePlayers) {
            Bukkit.broadcast(
                    Component.text("Der Timer konnte ")
                            .color(TextColor.color(255,255,255))
                            .append(Component.text("nicht gestartet")
                                    .color(TextColor.color(122, 0, 3))
                                    .decorate(TextDecoration.BOLD))
                            .append(Component.text(" werden!")
                                    .color(TextColor.color(255,255,255)))
            );
        }
        return false;
    }



    //Stopps the Timer.
    //Returns false, if the timer couldn't be stopped (it is already stopped) and true, if the timer was stopped successfully.
    public boolean stop() {
        if (!this.isRunning) {
            return false;
        }

        Main.getChallengeManager().toggleChallenges(false);

        Bukkit.getOnlinePlayers().forEach(p ->
            p.sendActionBar(
                Component.text("Der Timer wurde gestoppt!")
                        .color(TextColor.color(255, 177, 68))
                        .decorate(TextDecoration.BOLD)
            )
        );

        Main.getPlugin().getConfig().set("timer.seconds", seconds);
        Main.getPlugin().saveConfig();
        this.isRunning = false;
        runnable.cancel();

        return true;
    }
    public boolean stop(boolean messagePlayers) {
        boolean successful = stop();
        if (successful) {
            if (messagePlayers) {
                Bukkit.broadcast(
                        Component.text("Der Timer wurde erfolgreich ")
                                .color(TextColor.color(255, 255, 255))
                                .append(Component.text("gestoppt")
                                        .color(TextColor.color(255, 0, 0))
                                        .decorate(TextDecoration.BOLD))
                                .append(Component.text("!")
                                        .color(TextColor.color(255, 255, 255)))
                );
            }

            return true;
        }
        if (messagePlayers) {
            Bukkit.broadcast(
                    Component.text("Der Timer konnte ")
                            .color(TextColor.color(255,255,255))
                            .append(Component.text("nicht gestoppt")
                                    .color(TextColor.color(122, 0, 3))
                                    .decorate(TextDecoration.BOLD))
                            .append(Component.text(" werden!")
                                    .color(TextColor.color(255,255,255)))
            );
        }
        return false;
    }



    public boolean reset() {
        stop();
        Main.getPlugin().getConfig().set("timer.seconds", 0);
        Main.getPlugin().saveConfig();
        this.seconds=0;
        return true;
    }

    public boolean reset(boolean messagePlayers) {
        boolean successful = reset();
        if (successful) {
            if (messagePlayers) {
                Bukkit.broadcast(
                        Component.text("Der Timer wurde erfolgreich ")
                                .color(TextColor.color(255, 255, 255))
                                .append(Component.text("zurückgesetzt")
                                        .color(TextColor.color(0, 36, 254))
                                        .decorate(TextDecoration.BOLD))
                                .append(Component.text("!")
                                        .color(TextColor.color(255, 255, 255)))
                );
            }
            return true;
        }
        if (messagePlayers) {
            Bukkit.broadcast(
                    Component.text("Der Timer konnte ")
                            .color(TextColor.color(255,255,255))
                            .append(Component.text("nicht zurückgesetzt")
                                    .color(TextColor.color(122, 0, 3))
                                    .decorate(TextDecoration.BOLD))
                            .append(Component.text(" werden!")
                                    .color(TextColor.color(255,255,255)))
            );
        }
        return false;
    }









    //Getter & Setter

    public int getSeconds() {
        return seconds;
    }

    public boolean isRunning() {
        return isRunning;
    }







    //returns a list of all active Players, which is being reloaded every time the timer resumes

    public List<Player> getActivePlayers() {
        return activePlayers;
    }
}
