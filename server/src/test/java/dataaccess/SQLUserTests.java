package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLUserTests {
    static UserDAO users = new SQLUserDAO();

    @BeforeAll
    static void beforeAll() throws DataAccessException {
        users.deleteAllUsers();
    }

    @Test
    void addUserPositive() throws DataAccessException {
        users.addUser(new UserData("john", "passcode123", "john@123.com"));
        assert !users.listUsers().isEmpty();
    }

    @Test
    void addUserNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () ->{
            users.addUser(new UserData(null, "passcode123", "john@123.com"));
        });
    }

    @Test
    void listUsersPositive() throws DataAccessException {
        users.addUser(new UserData("john", "passcode123", "john@123.com"));
        users.addUser(new UserData("kass", "passcode123", "kass@123.com"));
        users.addUser(new UserData("jean", "passcode123", "jean@123.com"));
        assert users.listUsers().size() == 3;
    }

    @Test
    void getUserPositive() throws DataAccessException {
        users.addUser(new UserData("penelope", "passcode123", "yuhhhh@123.com"));
        users.addUser(new UserData("dangit", "dangit", "dangit@dangit.com"));
        users.addUser(new UserData("jean", "passcode123", "jean@123.com"));
        assert users.getUser("penelope").email().equals("yuhhhh@123.com");
    }

    @Test
    void getUserNegative() throws DataAccessException {
        users.addUser(new UserData("penelope", "passcode123", "yuhhhh@123.com"));
        users.addUser(new UserData("dangit", "dangit", "dangit@dangit.com"));
        users.addUser(new UserData("jean", "passcode123", "jean@123.com"));
        assert users.getUser("josh") == null;
    }

    @Test
    void deleteAllUsers() throws DataAccessException {
        users.addUser(new UserData("penelope", "passcode123", "yuhhhh@123.com"));
        users.addUser(new UserData("dangit", "dangit", "dangit@dangit.com"));
        users.addUser(new UserData("jean", "passcode123", "jean@123.com"));
        users.deleteAllUsers();
        assert users.listUsers().isEmpty();
    }
}