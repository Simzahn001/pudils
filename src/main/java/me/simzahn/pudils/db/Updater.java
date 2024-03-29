package me.simzahn.pudils.db;

import com.zaxxer.hikari.HikariDataSource;
import de.chojo.sadu.databases.MariaDb;
import de.chojo.sadu.updater.SqlUpdater;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

public class Updater {

    private final DataSource dataSource;

    public Updater(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void update() throws IOException, SQLException {
        SqlUpdater.builder(dataSource, MariaDb.get()).execute();
    }
}
