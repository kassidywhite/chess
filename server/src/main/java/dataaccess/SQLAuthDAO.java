package dataaccess;

import com.google.gson.Gson;
import model.AuthData;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLAuthDAO implements AuthDAO {

    public SQLAuthDAO() {
        try {
            DatabaseManager.createDatabase();
            Connection conn = DatabaseManager.getConnection();
            try (conn){
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
        CREATE TABLE IF NOT EXISTS tokens (
          `authToken` varchar(256) NOT NULL,
          `username` varchar(256) NOT NULL,
          PRIMARY KEY (`authToken`)
        )
        """
    };

    @Override
    public void addAuth(AuthData data) throws DataAccessException{
        var statement = "INSERT INTO tokens (authToken, username) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection()){
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(1, data.authToken());
            preparedStatement.setString(2, data.username());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Internal Service Error");
        }
    }

    @Override
    public String getAuthByUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()){
            var statement = "SELECT authToken, username FROM tokens WHERE username = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try(ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()){
                        return null;
                    }
                    String name = rs.getString("username");
                    if(name.equals(username)){
                        return rs.getString("authToken");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Internal Service Error");
        }
        return null;
    }

    @Override
    public String getUserByAuth(String token) throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()){
            var statement = "SELECT authToken, username FROM tokens WHERE authToken = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, token);
                try(ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()){
                        return null;
                    }
                    String auth = rs.getString("authToken");
                    if(auth.equals(token)){
                        return rs.getString("username");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Internal Service Error");
        }
        return null;
    }

    @Override
    public void deleteAuth(String token) throws DataAccessException{
        var statement = "DELETE FROM tokens WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection()){
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setString(1, token);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Internal Service Error");
        }
    }

    @Override
    public void deleteAllAuth() throws DataAccessException {
        var statement = "TRUNCATE TABLE tokens";
        try (Connection conn = DatabaseManager.getConnection()){
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Internal Service Error");
        }
    }

}
