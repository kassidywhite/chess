package client;

import model.request.LoginRequest;
import model.request.RegisterRequest;
import model.result.RegisterResult;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
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


}
