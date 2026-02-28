package server;

import dataaccess.MemoryGameDAO;
import io.javalin.*;
import io.javalin.http.Context;
import model.*;
import com.google.gson.Gson;
import model.result.DeleteResult;
import model.result.RegisterResult;
import service.Service;
import dataaccess.*;

public class Server {

    private final Javalin javalin;
    private final Service userService = new Service();
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
            RegisterResult register_result = userService.register(user);
            ctx.result(serializer.toJson(register_result));
        } catch (DataAccessException e){
            ctx.status(403);
            ctx.result(serializer.toJson(e.getMessage()));
        }
    }

    private void clear(Context ctx) {
        String authToken = ctx.header("Authorization");
        try{
            DeleteResult delete_result = userService.deleteAll(authToken);
            ctx.result(serializer.toJson(delete_result));
            ctx.status(200);
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(serializer.toJson(e.getMessage()));
        }

    }
}
