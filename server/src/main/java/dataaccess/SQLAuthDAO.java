package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {

    public SQLAuthDAO() {
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
        CREATE TABLE IF NOT EXISTS tokens (
          `authToken` varchar(256) NOT NULL,
          `username` varchar(256) NOT NULL,
          PRIMARY KEY (`authToken`)
        )
        """
    };

    @Override
    public void addAuth(AuthData data) {

    }

    @Override
    public String getAuthByUser(String username) {
        return "";
    }

    @Override
    public String getUserByAuth(String token) {
        return "";
    }

    @Override
    public void deleteAuth(String token) {

    }

    @Override
    public void deleteAllAuth() {

    }
}
