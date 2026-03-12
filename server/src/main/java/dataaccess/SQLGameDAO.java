package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.ArrayList;
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
    public int createNewGame(String gameName) throws DataAccessException {
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
        } catch (SQLException e) {
            throw new DataAccessException("Internal Service Error");
        }
        return 0;
    }

    @Override
    public void addGame(GameData game) throws DataAccessException {
        var statement = "INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection()){
            var ps = conn.prepareStatement(statement);
            String json = new Gson().toJson(game.game());
            ps.setInt(1, game.gameID());
            ps.setString(2, game.whiteUsername());
            ps.setString(3, game.blackUsername());
            ps.setString(4, game.gameName());
            ps.setString(5, json);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Internal Service Error");
        }
    }

    @Override
    public GameData getGameByName(String gameName) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameName, gameID, blackUsername, whiteUsername, game FROM games WHERE gameName = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, gameName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    var gameID = rs.getInt("gameID");
                    var whiteUser = rs.getString("whiteUsername");
                    var blackUser = rs.getString("blackUsername");
                    var name = rs.getString("gameName");
                    var gameAsString = rs.getString("game");
                    ChessGame game = new Gson().fromJson(gameAsString, ChessGame.class);
                    return new GameData(gameID, whiteUser, blackUser, name, game);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

        @Override
    public GameData getGameByID(int id) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()){
            var statement = "SELECT gameName, gameID, blackUsername, whiteUsername, game FROM games WHERE gameID = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, id);
                try(ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()){
                        return null;
                    }
                    var gameID = rs.getInt("gameID");
                    var whiteUser = rs.getString("whiteUsername");
                    var blackUser = rs.getString("blackUsername");
                    var name = rs.getString("gameName");
                    var gameAsString = rs.getString("game");
                    ChessGame game = new Gson().fromJson(gameAsString, ChessGame.class);
                    if(gameID == id){
                        return new GameData(gameID, whiteUser, blackUser, name, game);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Internal Service Error");
        }
        return null;
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        List<GameData> result = new ArrayList<>();
        var statement = "SELECT * FROM games";
        try (Connection conn = DatabaseManager.getConnection()){
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                try(ResultSet rs = ps.executeQuery()) {
                    while(rs.next()){
                        var gameID = rs.getInt("gameID");
                        var whiteUser = rs.getString("whiteUsername");
                        var blackUser = rs.getString("blackUsername");
                        var name = rs.getString("gameName");
                        var gameAsString = rs.getString("game");
                        ChessGame game = new Gson().fromJson(gameAsString, ChessGame.class);
                        result.add(new GameData(gameID, whiteUser, blackUser, name, game));
                    }
                    return result;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Internal Service Error");
        }
    }

    @Override
    public void deleteGame(String gameName) throws DataAccessException {
        var statement = "DELETE FROM games WHERE gameName = ?";
        try (Connection conn = DatabaseManager.getConnection()){
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setString(1, gameName);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Internal Service Error");
        }
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
