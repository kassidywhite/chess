package dataaccess;

import com.google.gson.Gson;
import model.UserData;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() {
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
            CREATE TABLE IF NOT EXISTS users (
                `username` varchar(256) NOT NULL,
                `password` varchar(256) NOT NULL,
                `email` varchar(256) NOT NULL,
               PRIMARY KEY (`username`)
            )
            """
    };

    @Override
    public void addUser(UserData data) {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection()){
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(1, data.username());
            preparedStatement.setString(2, data.password());
            preparedStatement.setString(3, data.email());

            preparedStatement.executeUpdate();

        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public ArrayList<UserData> listUsers() {
        return null;
    }

    @Override
    public UserData getUser(String username) {
        try (Connection conn = DatabaseManager.getConnection()){
            var statement = "SELECT username, password, email FROM users WHERE username = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setString(1, username);
                try(ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()){
                        return null;
                    }
                    String name = rs.getString("username");
                    if(name.equals(username)){
                        return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                    }
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException();
        }
        return null;
    }

    @Override
    public void deleteAllUsers() {
        var statement = "TRUNCATE TABLE users";
        try (Connection conn = DatabaseManager.getConnection()){
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
