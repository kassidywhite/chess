package dataaccess;

import model.UserData;

import javax.xml.crypto.Data;
import java.sql.Connection;
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
            CREATE TABLE IF NOT EXISTS user (
                `username` varchar(256) NOT NULL,
                `password` varchar(256) NOT NULL,
                `email` varchar(256) NOT NULL,
               PRIMARY KEY (`username`),
               INDEX(username)
            )
            """
    };

    @Override
    public void addUser(UserData data) {

    }

    @Override
    public ArrayList<UserData> listUsers() {
        return null;
    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void deleteAllUsers() {

    }
}
