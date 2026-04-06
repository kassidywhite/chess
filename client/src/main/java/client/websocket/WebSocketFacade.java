package client.websocket;

import com.google.gson.Gson;
import jakarta.websocket.*;
import server.Server;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                    @Override
                    public void onMessage(String message) {
                        ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                        notificationHandler.notify(notification);
                }
            });

        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new Exception("Oops something went wrong!");
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void enterGame(String authToken, int gameId) throws Exception {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameId);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException e) {
            throw new Exception("Failed to make connect action");
        }
    }

    public void leaveGame(String authToken, int gameId) throws Exception {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameId);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException e) {
            throw new Exception("Failed to make leave action");
        }
    }
}
