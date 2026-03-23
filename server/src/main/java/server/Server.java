package server;

import dataaccess.DataAccessException;
import io.javalin.*;
import io.javalin.http.Context;
import com.google.gson.Gson;
import model.request.JoinGameRequest;
import model.request.LoginRequest;
import model.request.NewGameRequest;
import model.request.RegisterRequest;
import model.result.*;
import service.Service;
import service.exceptions.AlreadyTakenException;
import service.exceptions.BadRequestException;
import service.exceptions.ServiceException;
import service.exceptions.UnauthorizedException;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final Service service = new Service();
    private final Gson serializer = new Gson();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        javalin.post("/game", this::createGame);
        javalin.get("/game", this::listGames);
        javalin.put("/game", this::joinGame);
        javalin.delete("/db", this::clear);

        // Register your endpoints and exception handlers here.

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void register(Context ctx) {
        try{
            RegisterRequest registerRequest = serializer.fromJson(ctx.body(), RegisterRequest.class);
            RegisterResult registerResult = service.register(registerRequest);
            ctx.result(serializer.toJson(registerResult));
        } catch (ServiceException e) {
            serviceExceptionHandler(ctx, e);
        }
    }

    private void login(Context ctx) {
        try{
            LoginRequest loginRequest = serializer.fromJson(ctx.body(), LoginRequest.class);
            LoginResult loginResult = service.login(loginRequest);
            ctx.result(serializer.toJson(loginResult));
        } catch (ServiceException e){
            serviceExceptionHandler(ctx, e);
        }
    }

    private void logout(Context ctx) {
        try{
            String authToken = ctx.header("Authorization");
            LogoutResult logoutResult = service.logout(authToken);
            ctx.result(serializer.toJson(logoutResult));
        } catch (ServiceException e){
            serviceExceptionHandler(ctx, e);
        }
    }

    private void createGame(Context ctx){
        try {
            String authToken = ctx.header("Authorization");
            NewGameRequest gameRequest = serializer.fromJson(ctx.body(), NewGameRequest.class);
            NewGameResult gameResult = service.createGame(gameRequest, authToken);
            ctx.result(serializer.toJson(gameResult));
        } catch (ServiceException e) {
            serviceExceptionHandler(ctx, e);
        }
    }

    private void listGames(Context ctx){
        try{
            String authToken = ctx.header("Authorization");
            ListGamesResult listGamesResult = service.listGames(authToken);
            ctx.result(serializer.toJson(listGamesResult));
        } catch (ServiceException e) {
            serviceExceptionHandler(ctx, e);
        }
    }

    private void joinGame(Context ctx) {
        try{
            String authToken = ctx.header("Authorization");
            JoinGameRequest joinRequest = serializer.fromJson(ctx.body(), JoinGameRequest.class);
            JoinGameResult joinResult = service.joinGame(joinRequest, authToken);
            ctx.result(serializer.toJson(joinResult));
        } catch (ServiceException e) {
            serviceExceptionHandler(ctx, e);
        }
    }

    public void clear(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
            DeleteResult deleteResult = service.deleteAll(authToken);
            ctx.result(serializer.toJson(deleteResult));
            ctx.status(200);
        } catch (ServiceException e) {
            serviceExceptionHandler(ctx, e);
        }

    }

    private void serviceExceptionHandler(Context ctx, ServiceException e){
        switch (e) {
            case BadRequestException ex -> {
                ctx.contentType("application/json");
                ctx.status(400);
                ctx.result(serializer.toJson(Map.of("message", e.getMessage())));
            }
            case UnauthorizedException ex -> {
                ctx.contentType("application/json");
                ctx.status(401);
                ctx.result(serializer.toJson(Map.of("message", e.getMessage())));
            }
            case AlreadyTakenException ex ->  {
                ctx.contentType("application/json");
                ctx.status(403);
                ctx.result(serializer.toJson(Map.of("message", e.getMessage())));
            }
            default -> {
                ctx.contentType("application/json");
                ctx.status(500);
                ctx.result(serializer.toJson(Map.of("message", e.getMessage())));
            }
        }
    }
}
