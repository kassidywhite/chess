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
        AuthData auth = new AuthData("penelope");
        tokens.addAuth(auth);
        String token = auth.authToken();
        tokens.deleteAuth(token);
        assert tokens.getAuthByUser("penelope") == null;
    }

    @Test
    void deleteAuthNegative() throws DataAccessException {
        AuthData auth = new AuthData("jerod");
        tokens.addAuth(auth);
        String token = auth.authToken();
        tokens.deleteAuth("123456");
        assert !tokens.getAuthByUser("jerod").isEmpty();
    }

    @Test
    void deleteAllAuth() throws DataAccessException {
        tokens.addAuth(new AuthData("I want cookies"));
        tokens.addAuth(new AuthData("C"));
        tokens.addAuth(new AuthData("O"));
        tokens.addAuth(new AuthData("O"));
        tokens.addAuth(new AuthData("K"));
        tokens.addAuth(new AuthData("I"));
        tokens.addAuth(new AuthData("E"));
        tokens.deleteAllAuth();
        assert tokens.getAuthByUser("I want cookies") == null;
    }
}