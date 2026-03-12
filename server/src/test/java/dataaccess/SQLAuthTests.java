package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLAuthTests {
    static AuthDAO tokens = new SQLAuthDAO();

    @BeforeAll
    static void beforeAll() throws DataAccessException {
        tokens.deleteAllAuth();
    }

    @Test
    void addAuthPositive() throws DataAccessException {
        tokens.addAuth(new AuthData("josh"));
        assert tokens.getAuthByUser("josh") != null;
    }

    @Test
    void addAuthNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () ->{
            tokens.addAuth(new AuthData(null));
        });
    }

    @Test
    void getAuthByUserPositive() throws DataAccessException {
        tokens.addAuth(new AuthData("harry"));
        tokens.addAuth(new AuthData("ron"));
        tokens.addAuth(new AuthData("hermione"));
        assert !tokens.getAuthByUser("ron").isEmpty();
    }

    @Test
    void getAuthByUserNegative() throws DataAccessException {
        tokens.addAuth(new AuthData("yuh"));
        assert tokens.getAuthByUser(null) == null;
    }

    @Test
    void getUserByAuthPositive() throws DataAccessException {
        String name = "asdfghjkl";
        AuthData auth = new AuthData(name);
        tokens.addAuth(auth);
        String token = auth.authToken();
        assert name.equals(tokens.getUserByAuth(token));
    }

    @Test
    void getUserByAuthNegative() throws DataAccessException {
        String name = "daniel";
        AuthData auth = new AuthData(name);
        String token = auth.authToken();
        assert !name.equals(tokens.getUserByAuth(token));
    }

    @Test
    void deleteAuthPositive() throws DataAccessException {

    }

    @Test
    void deleteAuthNegative() throws DataAccessException {

    }

    @Test
    void deleteAllAuth() throws DataAccessException {
    }
}