package me.simzahn.pudils.db;

import me.simzahn.pudils.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Setup {

    //a method to call at startup, which ensures the tables are set up correctly
    public static void InitialQuerries() {

        final String QUERRY = """
                CREATE TABLE IF NOT EXISTS player
                (
                    ID      INT AUTO_INCREMENT PRIMARY KEY,
                    name    TINYTEXT,
                    playing BOOLEAN  NOT NULL,
                    uuid    TINYTEXT NOT NULL UNIQUE
                );
                
                CREATE TABLE IF NOT EXISTS challenge
                (
                    ID      INT AUTO_INCREMENT PRIMARY KEY,
                    name    TINYTEXT NOT NULL,
                    active  BOOLEAN
                );
                
                CREATE TABLE IF NOT EXISTS attempt
                (
                    ID      INT AUTO_INCREMENT PRIMARY KEY,
                    time    INT NOT NULL,
                    nether  BOOLEAN,
                    end     BOOLEAN
                );
                
                CREATE TABLE IF NOT EXISTS attemptPlayer
                (
                    attemptID  INT NOT NULL,
                    playerID   INT NOT NULL,
                     
                    FOREIGN KEY (attemptID) REFERENCES attempt(ID),
                    FOREIGN KEY (playerID) REFERENCES player(ID)
                );
                
                CREATE TABLE IF NOT EXISTS attemptChallenge
                (
                    attemptID    INT NOT NULL,
                    challengeID  INT NOT NULL,
                    
                    FOREIGN KEY (attemptID) REFERENCES attempt(ID),
                    FOREIGN KEY (challengeID) REFERENCES challenge(ID)                  
                );
                
                """;

        try(Connection connection = Main.getPlugin().getHikari().getConnection();
            PreparedStatement stmt = connection.prepareStatement(QUERRY);) {

            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
