package client;

import chess.ChessGame;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import model.GameData;
import serverfacade.ServerFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Arrays;

import static ui.EscapeSequences.ERASE_LINE;

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
            case LoadGameMessage msg -> System.out.print(msg.getMessage());
            case NotificationMessage msg -> System.out.print(msg.getMessage());
            case ErrorMessage msg -> System.out.print(msg.getMessage());
            default ->
                System.out.println("oops something went wrong");
        }
        System.out.print("\n" + ERASE_LINE + "[LOGGED_IN] >>> ");
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "redraw" -> redraw();
                case "leave" -> leave();
                case "makemove" -> makeMove(params);
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
        GameData activeGame = postHandler.activeGame;
        state = State.SIGNEDIN;
        preHandler.state = State.SIGNEDIN;
        postHandler.state = State.SIGNEDIN;

        try {
            ws.leaveGame(token, activeGame.gameID());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "";
    }

    public String makeMove(String... params) {

        return "";
    }

    public String help() {
        return """
                You are currently playing/observing a game, please do one of the following:
                    "redraw" - view the board
                    "leave" - leave game or stop observing
                    "make move" <piece> <square> - to make move
                    "resign" - to forfeit the game
                    "highlight" <piece square> - to highlight legal moves for a certain piece
                """;
    }
}
