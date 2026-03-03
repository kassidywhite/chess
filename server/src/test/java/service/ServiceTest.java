package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.UserData;
import model.request.JoinGameRequest;
import model.request.LoginRequest;
import model.request.NewGameRequest;
import model.request.RegisterRequest;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.exceptions.BadRequestException;
import service.exceptions.ServiceException;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTest {

    @Test
    void registerPositive() {
        Service service = new Service();
        try{
            service.register(new RegisterRequest("john", "123abc", "john@gmail.com"));
        } catch (Exception ex) {
            fail();
        }

    }

    @Test
    void registerNegative() {
        Service service = new Service();
        assertThrows(BadRequestException.class, () ->
                service.register(new RegisterRequest("john", null, "john@email.com")));
    }

    @Test
    void loginPositive() {
        Service service = new Service();
        try{
            service.register(new RegisterRequest("john", "2241", "john@gmail.com"));
            service.login(new LoginRequest("john", "2241"));
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    void loginNegative() {
        Service service = new Service();
        try{
            service.register(new RegisterRequest("john", "iluvdogs", "dogs@gmail.com"));
        } catch (Exception ex) {
            fail();
        }
        assertThrows(ServiceException.class, () ->
                service.login(new LoginRequest("john", "iluvhorses")));
    }

    @Test
    void logoutPositive() {
        Service service = new Service();
        try{
            service.register(new RegisterRequest("Daniel", "daniel", "daniel@daniel.com"));
            service.login(new LoginRequest("Daniel", "daniel"));
            service.logout(service.authAccess.getAuthByUser("Daniel"));
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    void logoutNegative() {
        Service service = new Service();
        try{
            service.register(new RegisterRequest("Daniel", "daniel", "daniel@daniel.com"));
            service.login(new LoginRequest("Daniel", "daniel"));
        } catch (Exception ex) {
            fail();
        }
        assertThrows(ServiceException.class, () ->
                service.logout(service.authAccess.getAuthByUser("Shelby")));
    }

    @Test
    void createGamePositive() {
        Service service = new Service();
        try {
            service.register(new RegisterRequest("Daniel", "daniel", "daniel@daniel.com"));
            service.login(new LoginRequest("Daniel", "daniel"));
            service.createGame(new NewGameRequest("queens gambit"), service.authAccess.getAuthByUser("Daniel"));
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    void createGameNegative() {
        Service service = new Service();
        try {
            service.register(new RegisterRequest("Daniel", "daniel", "daniel@daniel.com"));
            service.login(new LoginRequest("Daniel", "daniel"));
        } catch (Exception ex) {
            fail();
        }
        assertThrows(ServiceException.class, () ->
                service.createGame(new NewGameRequest(null), service.authAccess.getAuthByUser("Daniel")));
    }

    @Test
    void listGamesPositive() {
        Service service = new Service();
        try{
            service.register(new RegisterRequest("Daniel", "daniel", "daniel@daniel.com"));
            service.login(new LoginRequest("Daniel", "daniel"));
            service.createGame(new NewGameRequest("queens gambit"), service.authAccess.getAuthByUser("Daniel"));
            service.register(new RegisterRequest("Jenny", "jenny", "daniel@daniel.com"));
            service.login(new LoginRequest("Jenny", "jenny"));
            service.createGame(new NewGameRequest("i luv chess"), service.authAccess.getAuthByUser("Jenny"));
            service.createGame(new NewGameRequest("yuhhh"), service.authAccess.getAuthByUser("Jenny"));
            service.listGames(service.authAccess.getAuthByUser("Jenny"));
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    void listGamesNegative() {
        Service service = new Service();
        try{
            service.register(new RegisterRequest("Jenny", "jenny", "daniel@daniel.com"));
            service.login(new LoginRequest("Jenny", "jenny"));
            service.createGame(new NewGameRequest("i luv chess"), service.authAccess.getAuthByUser("Jenny"));
            service.createGame(new NewGameRequest("yuhhh"), service.authAccess.getAuthByUser("Jenny"));
        } catch (Exception ex) {
            fail();
        }
        assertThrows(ServiceException.class, () ->
                service.listGames(service.authAccess.getAuthByUser("Daniel")));
    }

    void createJennyAndDanny(Service service){
        try{
            service.register(new RegisterRequest("Jenny", "jenny", "daniel@daniel.com"));
            service.login(new LoginRequest("Jenny", "jenny"));
            service.createGame(new NewGameRequest("i luv chess"), service.authAccess.getAuthByUser("Jenny"));
            service.register(new RegisterRequest("Daniel", "daniel", "daniel@daniel.com"));
            service.login(new LoginRequest("Daniel", "daniel"));
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    void joinGamePositive() {
        Service service = new Service();
        try {
            createJennyAndDanny(service);
            String token = service.authAccess.getAuthByUser("Daniel");
            int gameID = service.gameAccess.getGameByName("i luv chess").gameID();
            service.joinGame(new JoinGameRequest("WHITE", gameID), token);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    void joinGameNegative() {
        Service service = new Service();
        int id = service.gameAccess.getGameByName("i luv chess").gameID();
        String authToken = service.authAccess.getAuthByUser("Daniel");
        try {
            createJennyAndDanny(service);
            service.joinGame(new JoinGameRequest("WHITE", id), authToken);
        } catch (Exception ex) {
            fail();
        }
        assertThrows(ServiceException.class, () ->
                service.joinGame(new JoinGameRequest("WHITE", id), authToken));
    }

    @Test
    void deleteAll() {
        Service service = new Service();
        try {
            service.register(new RegisterRequest("Jenny", "jenny", "daniel@daniel.com"));
            service.login(new LoginRequest("Jenny", "jenny"));
            service.createGame(new NewGameRequest("i luv chess"), service.authAccess.getAuthByUser("Jenny"));
            service.createGame(new NewGameRequest("queens gambit"), service.authAccess.getAuthByUser("Jenny"));
            service.createGame(new NewGameRequest("todays challenge"), service.authAccess.getAuthByUser("Jenny"));
            service.deleteAll(service.authAccess.getAuthByUser("Jenny"));
        } catch (Exception ex) {
            fail();
        }
        assertEquals(0, service.gameAccess.listGames().size());
    }
}