package me.simzahn.pudils;

import com.zaxxer.hikari.HikariDataSource;
import me.simzahn.pudils.timer.Timer;
import me.simzahn.pudils.timer.TimerCom;
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
}
