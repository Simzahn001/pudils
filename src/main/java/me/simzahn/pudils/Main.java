package me.simzahn.pudils;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main plugin;
    private HikariDataSource hikari;

    @Override
    public void onEnable() {
        //Main Singleton
        plugin = this;

        //HikariCP Setup
        hikari = new HikariDataSource();
        hikari.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
        hikari.addDataSourceProperty("serverName", this.getConfig().get("db.ip"));
        hikari.addDataSourceProperty("port", this.getConfig().get("db.port"));
        hikari.addDataSourceProperty("databaseName", this.getConfig().get("db.name"));
        hikari.addDataSourceProperty("user", this.getConfig().get("db.user"));
        hikari.addDataSourceProperty("password", this.getConfig().get("db.password"));

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
}
