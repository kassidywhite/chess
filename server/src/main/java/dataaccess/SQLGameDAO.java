package dataaccess;

import model.GameData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() {
        try {
            DatabaseManager.createDatabase();
            try (Connection conn = DatabaseManager.getConnection()){
                for (String statement : createStatements) {
                    try(var preparedStatement = conn.prepareStatement(statement)) {
                        preparedStatement.executeUpdate();
                    }
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS games (
              `gameName` varchar(256) NOT NULL,
              `gameID` INT NOT NULL AUTO_INCREMENT,
              `blackUsername` varchar(256),
              `whiteUsername` varchar(256),
              `game` TEXT NOT NULL,
              PRIMARY KEY (`gameID`)
            )
            """
    };

    @Override
    public int createNewGame(String gameName) {
        return 0;
    }

    @Override
    public void addGame(GameData game) {

    }

    @Override
    public GameData getGameByName(String gameName) {
        return null;
    }

    @Override
    public GameData getGameByID(int id) {
        return null;
    }

    @Override
    public List<GameData> listGames() {
        return List.of();
    }

    @Override
    public void deleteGame(String gameName) {

    }

    @Override
    public void deleteAllGames() {
        var statement = "TRUNCATE TABLE games";
        try (Connection conn = DatabaseManager.getConnection()){
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
