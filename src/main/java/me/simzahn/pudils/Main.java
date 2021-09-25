package me.simzahn.pudils;

import com.zaxxer.hikari.HikariDataSource;
import me.simzahn.pudils.commands.DifficultyCom;
import me.simzahn.pudils.listeners.EntityRegenerateEvent;
import me.simzahn.pudils.timer.Timer;
import me.simzahn.pudils.timer.TimerCom;
import me.simzahn.pudils.util.Difficulty;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Time;

public final class Main extends JavaPlugin {

    private static Timer timer;
    private static Main plugin;
    private HikariDataSource hikari;

    @Override
    public void onEnable() {
        //Main Singleton
        plugin = this;
        timer = new Timer();

        //HikariCP Setup
        hikari = new HikariDataSource();
        hikari.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
        hikari.addDataSourceProperty("serverName", this.getConfig().get("db.ip"));
        hikari.addDataSourceProperty("port", this.getConfig().get("db.port"));
        hikari.addDataSourceProperty("databaseName", this.getConfig().get("db.name"));
        hikari.addDataSourceProperty("user", this.getConfig().get("db.user"));
        hikari.addDataSourceProperty("password", this.getConfig().get("db.password"));


        getCommand("timer").setExecutor(new TimerCom());
        getCommand("timer").setTabCompleter(new TimerCom());

        getCommand("difficutly").setExecutor(new DifficultyCom());
        getCommand("difficulty").setExecutor(new DifficultyCom());


        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new EntityRegenerateEvent(), this);
    }

    @Override
    public void onDisable() {

        if(hikari != null) {
            hikari.close();
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

}
