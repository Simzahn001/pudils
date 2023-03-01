package me.simzahn.pudils.timer;

import me.simzahn.pudils.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Timer {

    private int seconds;
    private boolean isRunning = false;
    private BukkitTask runnable;

    private String SELECT = "SELECT uuid,playing FROM player";

    public Timer() {
        this.seconds = Main.getPlugin().getConfig().getInt("timer.seconds");
    }

    public void start() {
        reset();
        resume();
    }

    public void resume() {
        if(this.isRunning) {
            Bukkit.getOnlinePlayers().forEach( player -> player.sendMessage("§4§fDer Timer konnte nicht gestartet werden, weil er bereits läuft!"));
        }else {
            isRunning = true;
            Main.getPlugin().getChallengeManager().toggleChallenges(true);
            this.runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    //just for safety
                    if(!isRunning) {
                        stop();
                    }

                    for(Player currentPlayer : Bukkit.getOnlinePlayers()) {
                        currentPlayer.sendActionBar("§6§l" + ((int)seconds/60/60<10?"0"+(int)seconds/60/60:(int)seconds/60/60) + ":" + (seconds/60%60<10?"0"+seconds/60%60:seconds/60%60) + ":" + (seconds%60<10?"0"+seconds%60:seconds%60));
                    }
                    seconds++;
                }
            }.runTaskTimer(Main.getPlugin(), 20, 20);
        }
    }

    public void stop() {
        if(!this.isRunning) {
            Bukkit.getOnlinePlayers().forEach( player -> player.sendMessage("§4§fDer Timer konnte nicht gestoppt werden, weil er bereit gestoppt wurde!") );
        }else {
            Main.getPlugin().getChallengeManager().toggleChallenges(false);
            for (Player currentPlayer :  Bukkit.getOnlinePlayers()) {
                currentPlayer.sendActionBar("§4§fDer Timer wurde gestoppt!");
            }
            Main.getPlugin().getConfig().set("timer.seconds", seconds);
            Main.getPlugin().saveConfig();
            this.isRunning = false;
            runnable.cancel();
        }
    }

    public void reset() {
        stop();
        Main.getPlugin().getConfig().set("timer.seconds", 0);
        Main.getPlugin().saveConfig();
        this.seconds=0;
    }









    //Getter & Setter

    public int getSeconds() {
        return seconds;
    }

    public boolean isRunning() {
        return isRunning;
    }
}
