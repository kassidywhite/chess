package service;

import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import model.result.DeleteResult;
import model.result.RegisterResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Server;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {
    static private Server server;
    static final Service service = new Service();

//    @BeforeEach
//    void clear(){
//        DeleteResult res = service.deleteAll();
//    }

    @Test
    void addUser() {
        var user = new UserData("kassidy", "123abc", "kassidywhite@gmail.com");
        service.addUser(user);

        Collection<UserData> users = service.listUsers();
        assertEquals(1, users.size());
        assertTrue(users.contains(user));
    }

    @Test
    void listUsers() {
        List<UserData> expected = new ArrayList<>();
        service.addUser(new UserData("joe", "iluvpizza", "joe@gmail.com"));
        service.addUser(new UserData("felicia", "yuhhh", "blah@gmail.com"));
        service.addUser(new UserData("tommy", "12345678", "tombom@gmail.com"));

        Collection<UserData> actual = service.listUsers();
        assertIterableEquals(expected, actual);
    }

//    @Test
//    void checkAuth() throws DataAccessException {
//        Service usrService = new Service();
//        UserData user = new UserData("blah", "blah", "blah@gmail.com");
//        RegisterResult result = usrService.register(user);
//    }
}
