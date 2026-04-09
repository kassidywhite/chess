package client;

import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import serverfacade.ServerFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Arrays;

public class InGame implements NotificationHandler {
    private final ServerFacade server;
    private final String serverUrl;
    private final Prelogin preHandler;
    private final WebSocketFacade ws;
    public State state = State.INGAME;

    public InGame(String serverUrl, ServerFacade server, Prelogin preHandler, WebSocketFacade ws) throws Exception {
        this.server = server;
        this.serverUrl = serverUrl;
        this.preHandler = preHandler;
        this.ws = ws;
    }

    @Override
    public void notify(ServerMessage notification) {
        switch (notification) {
            case LoadGameMessage msg -> System.out.println(msg.getMessage());
            case NotificationMessage msg -> System.out.println(msg.getMessage());
            case ErrorMessage msg -> System.out.println(msg.getMessage());
            default ->
                System.out.println("oops something went wrong");
        }
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
//                case "redraw" -> redraw();
                case "leave" -> leave();
//                case "makemove" -> makeMove(params);
//                case "resign" -> resign();
//                case "highlight" -> highlightLegalMoves(params);
                default -> help();
            };
        } catch (Exception e) {
            return preHandler.clientExceptionHandler(e);
        }
    }

    public String leave() {
        state = State.SIGNEDIN;
        return "";
    }

    public String help() {
        return """
                Options:
                    "redraw" - view the board
                    "leave" - leave game
                    "make move" <piece> <square> - to make move
                    "resign" - to forfeit the game
                    "highlight" <piece square> - to highlight legal moves for a certain piece
                """;
    }
}
