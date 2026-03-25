package client;

import model.request.JoinGameRequest;
import model.request.LoginRequest;
import model.request.NewGameRequest;
import model.request.RegisterRequest;
import model.result.*;
import org.junit.jupiter.api.*;
import server.Server;
import serverFacade.ServerFacade;
import service.Service;
import service.exceptions.ServiceException;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private static Service service;

    @BeforeAll
    public static void init() {
        server = new Server();
        service = new Service();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @BeforeEach
    void setUp() throws ServiceException {
        service.deleteAll();
    }


    @Test
    void registerTestPositive() throws Exception{
        RegisterRequest regReq = new RegisterRequest("john", "john", "john@gmail.com");
        RegisterResult result = serverFacade.register(regReq);
        assertEquals("john", result.username());
    }

    @Test
    void registerTestNegative() throws Exception {
        RegisterRequest regReq = new RegisterRequest(null, "1234", "john@gmail.com");
        assertThrows(Exception.class, () -> {
            serverFacade.register(regReq);
        });
    }

    @Test
    void loginTestPositive() throws Exception {
        RegisterRequest regReq = new RegisterRequest("john", "john", "john@gmail.com");
        RegisterResult result = serverFacade.register(regReq);
        LoginRequest logReq = new LoginRequest("john", "john");
        assertDoesNotThrow(() -> {
            serverFacade.login(logReq);
        });
    }

    @Test
    void loginTestNegative() throws Exception {
        RegisterRequest regReq = new RegisterRequest("john", "john", "john@gmail.com");
        RegisterResult result = serverFacade.register(regReq);
        LoginRequest logReq = new LoginRequest("kathy", "john");
        assertThrows(Exception.class, () -> {
            serverFacade.login(logReq);
        });
    }

    @Test
    void logoutTestPositive() throws Exception {
        RegisterRequest regReq = new RegisterRequest("lexi", "yoyo123", "lexi@gmail.com");
        RegisterResult result = serverFacade.register(regReq);
        String token = service.authAccess.getAuthByUser("lexi");
        assertDoesNotThrow(() -> {
            serverFacade.logout(token);
        });
    }

    @Test
    void logoutTestNegative() throws Exception {
        RegisterRequest regReqPenelope = new RegisterRequest("penelope", "yoyo123", "penelope@gmail.com");
        RegisterResult resultPenelope = serverFacade.register(regReqPenelope);
        RegisterRequest regReqDan = new RegisterRequest("daniel", "yoyoyo", "daniel@gmail.com");
        RegisterResult resultDan = serverFacade.register(regReqDan);
        String token = service.authAccess.getAuthByUser("penelope");
        LogoutResult logoutResult = serverFacade.logout(token);
        LoginRequest logReqDan = new LoginRequest("daniel", "yoyoyo");
        assertThrows(Exception.class, () -> {
            serverFacade.logout(token);
        });
    }

    @Test
    void createGameTestPositive() throws Exception {
        RegisterRequest regReq = new RegisterRequest("yoyo", "yoyo123", "yoyo@gmail.com");
        RegisterResult result = serverFacade.register(regReq);
        NewGameRequest gameReq = new NewGameRequest("yoyo's game");
        String token = service.authAccess.getAuthByUser("yoyo");
        NewGameResult gameResult = serverFacade.createGame(gameReq, token);
        ListGamesResult listCheck = service.listGames(token);
        assertEquals(1, listCheck.games().size());
    }

    @Test
    void createGameTestNegative() throws Exception {
        RegisterRequest regReq = new RegisterRequest("matt", "matt", "matt@gmail.com");
        RegisterResult result = serverFacade.register(regReq);
        String token = "1234567890-jklmnopqrs";
        NewGameRequest gameReq = new NewGameRequest("matt's game");
        assertThrows(Exception.class, () -> {
            serverFacade.createGame(gameReq, token);
        });
    }

    @Test
    void listGamesTestPositive() throws Exception {
        String token = createGamesWithTest();
        ListGamesResult listRes = serverFacade.listGames(token);
        assertEquals(3, listRes.games().size());
    }

    @Test
    void listGamesTestNegative() throws Exception {
        String token = createGamesWithTest();
        String badToken = "1234567890-jklmnopqrs";
        assertThrows(Exception.class, () -> {
            serverFacade.listGames(badToken);
        });
    }

    @Test
    void joinGameTestPositive() throws Exception {
        RegisterRequest regReq = new RegisterRequest("blah", "blah", "blah@gmail.com");
        RegisterResult result = serverFacade.register(regReq);
        String token = service.authAccess.getAuthByUser("blah");
        NewGameRequest gameReq = new NewGameRequest("blah_game");
        NewGameResult gameRes = serverFacade.createGame(gameReq, token);
        JoinGameRequest request = new JoinGameRequest("WHITE", 1);
        assertDoesNotThrow(() -> {
            serverFacade.joinGame(request, token);
        });
    }

    @Test
    void joinGameTestNegative() throws Exception {
        String token = createGamesWithTest();
        JoinGameRequest request = new JoinGameRequest(null, 2);
        assertThrows(Exception.class, () -> {
            serverFacade.joinGame(request, token);
        });
    }

    String createGamesWithTest() throws Exception {
        RegisterRequest regReq = new RegisterRequest("test", "test", "test@gmail.com");
        RegisterResult result = serverFacade.register(regReq);
        String token = service.authAccess.getAuthByUser("test");
        NewGameRequest gameReq1 = new NewGameRequest("test game");
        NewGameResult gameResult1 = serverFacade.createGame(gameReq1, token);
        NewGameRequest gameReq2 = new NewGameRequest("lets play chesssss");
        NewGameResult gameResult2 = serverFacade.createGame(gameReq2, token);
        NewGameRequest gameReq3 = new NewGameRequest("rahhhhhhh");
        NewGameResult gameResult3 = serverFacade.createGame(gameReq3, token);
        return token;
    }
}
