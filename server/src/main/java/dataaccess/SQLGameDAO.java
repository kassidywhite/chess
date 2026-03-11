package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

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
        var statement = "INSERT INTO games (gameName, game) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection()){
            var preparedStatement = conn.prepareStatement(statement, RETURN_GENERATED_KEYS);
            ChessGame game = new ChessGame();
            String json = new Gson().toJson(game);
            preparedStatement.setString(1, gameName);
            preparedStatement.setString(2, json);

            preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();
            if(rs.next()) {
                return rs.getInt(1);
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public void addGame(GameData game) {

    }

    @Override
    public GameData getGameByName(String gameName) {
        try (Connection conn = DatabaseManager.getConnection()){
            var statement = "SELECT gameName, gameID, blackUsername, whiteUsername, game FROM games WHERE gameName = ?";
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
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
