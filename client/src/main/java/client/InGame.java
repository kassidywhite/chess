package client;

import chess.ChessGame;
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
    Postlogin postHandler;

    public InGame(String serverUrl, ServerFacade server, Prelogin preHandler, Postlogin postHandler, WebSocketFacade ws) {
        this.server = server;
        this.serverUrl = serverUrl;
        this.preHandler = preHandler;
        this.postHandler = postHandler;
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
                case "redraw" -> redraw();
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

    public String redraw() {
        String username = preHandler.currentUser.username();
        boolean isBlack = postHandler.activeGame.blackUsername() != null &&
                postHandler.activeGame.blackUsername().equals(username);
        if(isBlack) {
            ChessBoardRender.render(postHandler.activeGame.game().getBoard(), "black");
        } else {
            ChessBoardRender.render(postHandler.activeGame.game().getBoard(), "white");
        }
        return "";
    }

    public String leave() {
        String token = preHandler.currentUser.authToken();
        //ws.leaveGame(token, gameID);
        state = State.SIGNEDIN;
        // ws.leaveGame();
        return "leave";
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
