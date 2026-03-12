package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.ArrayList;

public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() {
        try {
            DatabaseManager.createDatabase();
            try (Connection conn = DatabaseManager.getConnection()){
                for (String statement : statements) {
                    try(var preparedStatement = conn.prepareStatement(statement)) {
                        preparedStatement.executeUpdate();
                    }
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private final String[] statements = {
            """
            CREATE TABLE IF NOT EXISTS users (
                `username` varchar(256) NOT NULL,
                `password` varchar(256) NOT NULL,
                `email` varchar(256) NOT NULL,
               PRIMARY KEY (`username`)
            )
            """
    };

    @Override
    public void addUser(UserData data) throws DataAccessException{
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection()){
            var preparedStatement = conn.prepareStatement(statement);
            String hashedPassword = BCrypt.hashpw(data.password(), BCrypt.gensalt());
            preparedStatement.setString(1, data.username());
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, data.email());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Internal Service Error");
        }
    }

    @Override
    public ArrayList<UserData> listUsers() throws DataAccessException {
        ArrayList<UserData> result = new ArrayList<>();
        var statement = "SELECT * FROM users";
        try (Connection conn = DatabaseManager.getConnection()){
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                try(ResultSet rs = ps.executeQuery()) {
                    while(rs.next()){
                        var name = rs.getString("username");
                        var pass = rs.getString("password");
                        var email = rs.getString("email");
                        result.add(new UserData(name, pass, email));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Internal Service Error");
        }
        return result;
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()){
            var statement = "SELECT username, password, email FROM users WHERE username = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setString(1, username);
                try(ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()){
                        return null;
                    }
                    String name = rs.getString("username");
                    return new UserData(rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Internal Service Error");
        }
    }

    @Override
    public void deleteAllUsers() throws DataAccessException {
        var statement = "TRUNCATE TABLE users";
        try (Connection conn = DatabaseManager.getConnection()){
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.executeUpdate();
        } catch (SQLException  e) {
            throw new DataAccessException("Internal Service Error");
        }
    }
}
