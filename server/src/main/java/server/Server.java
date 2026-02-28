package server;

import com.google.gson.JsonObject;
import io.javalin.*;
import io.javalin.http.Context;
import model.*;
import com.google.gson.Gson;
import model.result.DeleteResult;
import model.result.RegisterResult;
import service.Service;
import dataaccess.*;
import service.exceptions.AlreadyTakenException;
import service.exceptions.BadRequestException;
import service.exceptions.ServiceException;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final Service service = new Service();
    private final Gson serializer = new Gson();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.post("/user", this::register);
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
            switch (e) {
                case BadRequestException ex -> {
                    ctx.contentType("application/json");
                    ctx.status(400);
                    ctx.result(serializer.toJson(Map.of("message", e.getMessage())));
                    //"Error: " + ex.getMessage();
                }
                case AlreadyTakenException ex ->  {
                    ctx.contentType("application/json");
                    ctx.status(403);
                    ctx.result(serializer.toJson(Map.of("message", e.getMessage())));
                }
                    //"Error: " + e.getMessage();
                default -> {
                    ctx.contentType("application/json");
                    ctx.status(500);
                    ctx.result(serializer.toJson(Map.of("message", e.getMessage())));
                }
            }
        }
    }

    private void clear(Context ctx) {
        String authToken = ctx.header("Authorization");
        DeleteResult delete_result = service.deleteAll(authToken);
        ctx.result(serializer.toJson(delete_result));
        ctx.status(200);

    }
}
