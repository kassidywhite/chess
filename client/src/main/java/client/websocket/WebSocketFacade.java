package client.websocket;
import com.sun.nio.sctp.NotificationHandler;

import io.javalin.http.Handler;
import io.javalin.http.HandlerType;
import io.javalin.router.Endpoint;
import io.javalin.router.EndpointMetadata;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class WebSocketFacade extends Endpoint {


    public WebSocketFacade(@NotNull HandlerType method, @NotNull String path, @NotNull Set<? extends EndpointMetadata> metadata, @NotNull Handler handler) {
        super(method, path, metadata, handler);
    }
}
