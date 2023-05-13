package me.simzahn.pudils;

import com.zaxxer.hikari.HikariDataSource;
import me.simzahn.pudils.challenges.ChallengeManager;
import me.simzahn.pudils.commands.DifficultyCom;
import me.simzahn.pudils.commands.ResetCom;
import me.simzahn.pudils.commands.TeamCom;
import me.simzahn.pudils.db.Updater;
import me.simzahn.pudils.death.DamageListener;
import me.simzahn.pudils.listeners.EntityRegenerateListener;
import me.simzahn.pudils.listeners.InventoryClickListener;
import me.simzahn.pudils.listeners.JoinListener;
import me.simzahn.pudils.timer.Timer;
import me.simzahn.pudils.timer.TimerCom;
import me.simzahn.pudils.util.Difficulty;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.SQLException;

public final class Main extends JavaPlugin {

    private static Timer timer;
    private static Main plugin;
    private HikariDataSource hikari;
    private ChallengeManager challengeManager;
    private PluginManager pluginManager;

    @Override
    public void onEnable() {
        //Main Singleton
        plugin = this;

        //HikariCP Setup
        hikari = new HikariDataSource();
        hikari.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
        try {
            hikari.addDataSourceProperty("serverName", this.getConfig().get("db.ip"));
            hikari.addDataSourceProperty("port", this.getConfig().get("db.port"));
            hikari.addDataSourceProperty("databaseName", this.getConfig().get("db.name"));
            hikari.addDataSourceProperty("user", this.getConfig().get("db.user"));
            hikari.addDataSourceProperty("password", this.getConfig().get("db.password"));
        } catch (NullPointerException exception) {
            Bukkit.broadcast(
                    Component.text("Die Login-Daten für die DB konnten nicht aus der Config File gelesen werden!")
                            .color(TextColor.color(255, 0, 0))
                            .decorate(TextDecoration.BOLD)
            );
        }

        //SADU Updater
        Updater saduUpdater = new Updater(hikari);
        try {
            saduUpdater.update();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        //set up the timer
        timer = new Timer();

        //register challenges
        challengeManager = new ChallengeManager();

        //register commands
        getCommand("timer").setExecutor(new TimerCom());
        getCommand("timer").setTabCompleter(new TimerCom());

        getCommand("difficulty").setExecutor(new DifficultyCom());
        getCommand("difficulty").setTabCompleter(new DifficultyCom());

        getCommand("team").setExecutor(new TeamCom());
        getCommand("team").setTabCompleter(new TeamCom());

        getCommand("reset").setExecutor(new ResetCom());

        //register listeners
        pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new EntityRegenerateListener(), this);
        pluginManager.registerEvents(new InventoryClickListener(), this);
        pluginManager.registerEvents(new JoinListener(), this);
        pluginManager.registerEvents(new DamageListener(), this);
        pluginManager.registerEvents(new LeaveListener(), this);
        pluginManager.registerEvents(new ResetCom(), this);

        //initialize config for difficulty
        getConfig().set("difficulty", Difficulty.NORMAl.name());
        saveConfig();

    }

    @Override
    public void onDisable() {

        if(hikari != null) {
            hikari.close();
        }
        if (timer != null) {
            timer.stop();
        }

    }

    public static Main getPlugin() {
        return plugin;
    }

    public HikariDataSource getHikari() {
        return hikari;
    }

    public static Timer getTimer() {
        return timer;
    }

    public static Difficulty getDifficulty() {
        return Difficulty.valueOf(plugin.getConfig().getString("difficulty"));
    }

    public static void setDifficulty(Difficulty difficulty) {
        plugin.getConfig().set("difficulty", difficulty.name());
        Main.getPlugin().saveConfig();
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(ChatColor.GREEN + "Die Difficulty wurde auf " + difficulty.getName() + "§r" + ChatColor.GREEN + " gesetzt"));

        if (difficulty == Difficulty.HALF_HEART) {
            for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
                currentPlayer.setHealth(1);
            }
        } else {
            for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
                currentPlayer.setHealth(20);
            }
        }

    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public ChallengeManager getChallengeManager() {
        return challengeManager;
    }
}
