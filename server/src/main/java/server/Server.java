package server;

import dataaccess.DataAccessException;
import io.javalin.*;
import io.javalin.http.Context;
import com.google.gson.Gson;
import model.GameData;
import model.request.JoinGameRequest;
import model.request.LoginRequest;
import model.request.NewGameRequest;
import model.request.RegisterRequest;
import model.result.*;
import server.websocket.WebSocketHandler;
import service.Service;
import service.exceptions.AlreadyTakenException;
import service.exceptions.BadRequestException;
import service.exceptions.ServiceException;
import service.exceptions.UnauthorizedException;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final Service service = new Service();
    private static final Gson SERIALIZER = new Gson();
    private final WebSocketHandler webSocketHandler;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        javalin.post("/game", this::createGame);
        javalin.get("/game", this::listGames);
        javalin.put("/game", this::joinGame);
        javalin.delete("/db", this::clear);

        webSocketHandler = new WebSocketHandler(this);

        javalin.ws("/ws", ws -> {
            ws.onConnect(webSocketHandler);
            ws.onMessage(webSocketHandler);
            ws.onClose(webSocketHandler);
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    public GameData getGame(int gameId) throws DataAccessException {
        return service.gameAccess.getGameByID(gameId);
    }

    public void updateGame(GameData gameData) throws DataAccessException {
        service.updateGame(gameData);
    }

    private void register(Context ctx) {
        try{
            RegisterRequest registerRequest = SERIALIZER.fromJson(ctx.body(), RegisterRequest.class);
            RegisterResult registerResult = service.register(registerRequest);
            ctx.result(SERIALIZER.toJson(registerResult));
        } catch (ServiceException e) {
            serviceExceptionHandler(ctx, e);
        }
    }

    private void login(Context ctx) {
        try{
            LoginRequest loginRequest = SERIALIZER.fromJson(ctx.body(), LoginRequest.class);
            LoginResult loginResult = service.login(loginRequest);
            ctx.result(SERIALIZER.toJson(loginResult));
        } catch (ServiceException e){
            serviceExceptionHandler(ctx, e);
        }
    }

    private void logout(Context ctx) {
        try{
            String authToken = ctx.header("Authorization");
            LogoutResult logoutResult = service.logout(authToken);
            ctx.result(SERIALIZER.toJson(logoutResult));
        } catch (ServiceException e){
            serviceExceptionHandler(ctx, e);
        }
    }

    private void createGame(Context ctx){
        try {
            String authToken = ctx.header("Authorization");
            NewGameRequest gameRequest = SERIALIZER.fromJson(ctx.body(), NewGameRequest.class);
            NewGameResult gameResult = service.createGame(gameRequest, authToken);
            ctx.result(SERIALIZER.toJson(gameResult));
        } catch (ServiceException e) {
            serviceExceptionHandler(ctx, e);
        }
    }

    private void listGames(Context ctx){
        try{
            String authToken = ctx.header("Authorization");
            ListGamesResult listGamesResult = service.listGames(authToken);
            ctx.result(SERIALIZER.toJson(listGamesResult));
        } catch (ServiceException e) {
            serviceExceptionHandler(ctx, e);
        }
    }

    private void joinGame(Context ctx) {
        try{
            String authToken = ctx.header("Authorization");
            JoinGameRequest joinRequest = SERIALIZER.fromJson(ctx.body(), JoinGameRequest.class);
            JoinGameResult joinResult = service.joinGame(joinRequest, authToken);
            ctx.result(SERIALIZER.toJson(joinResult));
        } catch (ServiceException e) {
            serviceExceptionHandler(ctx, e);
        }
    }

    public void clear(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
            DeleteResult deleteResult = service.deleteAll();
            ctx.result(SERIALIZER.toJson(deleteResult));
            ctx.status(200);
        } catch (ServiceException e) {
            serviceExceptionHandler(ctx, e);
        }

    }

    public boolean confirmAuth(String user) throws DataAccessException {
        return service.authAccess.getAuthByUser(user) != null;
    }

    public String getUserByAuth(String token) throws DataAccessException {
        return service.authAccess.getUserByAuth(token);
    }

    public static void serviceExceptionHandler(Context ctx, ServiceException e){
        switch (e) {
            case BadRequestException ex -> {
                ctx.contentType("application/json");
                ctx.status(400);
                ctx.result(SERIALIZER.toJson(Map.of("message", e.getMessage())));
            }
            case UnauthorizedException ex -> {
                ctx.contentType("application/json");
                ctx.status(401);
                ctx.result(SERIALIZER.toJson(Map.of("message", e.getMessage())));
            }
            case AlreadyTakenException ex ->  {
                ctx.contentType("application/json");
                ctx.status(403);
                ctx.result(SERIALIZER.toJson(Map.of("message", e.getMessage())));
            }
            default -> {
                ctx.contentType("application/json");
                ctx.status(500);
                ctx.result(SERIALIZER.toJson(Map.of("message", e.getMessage())));
            }
        }
    }
}
