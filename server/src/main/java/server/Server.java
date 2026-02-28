package server;

import com.google.gson.JsonObject;
import io.javalin.*;
import io.javalin.http.Context;
import model.*;
import com.google.gson.Gson;
import model.request.LoginRequest;
import model.result.DeleteResult;
import model.result.LoginResult;
import model.result.RegisterResult;
import service.Service;
import dataaccess.*;
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
            UserData user = serializer.fromJson(ctx.body(), UserData.class);
            RegisterResult register_result = service.register(user);
            ctx.result(serializer.toJson(register_result));
        } catch (ServiceException e) {
            serviceExceptionHandler(ctx, e);
        }
    }

    private void login(Context ctx) {
        try{
            LoginRequest login_request = serializer.fromJson(ctx.body(), LoginRequest.class);
            LoginResult login_result = service.login(login_request);
            ctx.result(serializer.toJson(login_result));
        } catch (ServiceException e){
            serviceExceptionHandler(ctx, e);
        }
    }

    private void clear(Context ctx) {
        String authToken = ctx.header("Authorization");
        DeleteResult delete_result = service.deleteAll(authToken);
        ctx.result(serializer.toJson(delete_result));
        ctx.status(200);

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
