package me.simzahn.pudils.challenges;

import me.simzahn.pudils.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ChallengeManager {

    private List<Challenge> registeredChallenges;

    public ChallengeManager() {}

    //register a new challenge
    public void registerChallenge(Challenge challenge) {
        registeredChallenges.add(challenge);

        //check if the challenge is already registered in the database
        final String SELECT = "SELECT ID FROM challenge WHERE name=?";
        try(Connection connection = Main.getPlugin().getHikari().getConnection();
            PreparedStatement stmtSelect = connection.prepareStatement(SELECT);) {

            stmtSelect.setString(1, challenge.getName());

            ResultSet resultSelect = stmtSelect.executeQuery();

            if (!resultSelect.next()) {
                //register the challenge
                final String INSERT = "INSERT INTO challenge (name, displayName, active) VALUES(name=?, displayName=?, active=?)";
                PreparedStatement stmtInsert = connection.prepareStatement(INSERT);
                stmtInsert.setString(1, challenge.getName());
                stmtInsert.setString(2, challenge.getDisplayName());
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


    //@TODO register/unregister the listener/scheduler on start/stop of the Timer in Timer.java
}
