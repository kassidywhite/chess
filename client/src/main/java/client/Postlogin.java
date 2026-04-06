package client;

import chess.ChessBoard;
import client.websocket.WebSocketFacade;
import model.GameData;
import model.request.*;
import model.result.*;
import serverfacade.ServerFacade;
import client.websocket.NotificationHandler;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Arrays;

import static client.State.*;
import static ui.EscapeSequences.*;

public class Postlogin implements NotificationHandler {
    private final ServerFacade server;
    private final String serverUrl;
    private final Prelogin preHandler;
    private final WebSocketFacade ws;
    public State state = SIGNEDIN;
    private InGame inGameHandler;

    public Postlogin(String serverUrl, ServerFacade server, Prelogin preHandler) throws Exception {
        this.server = server;
        this.serverUrl = serverUrl;
        this.preHandler = preHandler;
        ws = new WebSocketFacade(serverUrl, this);
        inGameHandler = new InGame(serverUrl, server, preHandler, ws);
    }

    @Override
    public void notify(ServerMessage notification) {
        switch(notification) {
            case NotificationMessage msg ->
                    System.out.println(SET_TEXT_COLOR_BLUE + msg.getMessage());
            case ErrorMessage msg ->
                    System.out.println(SET_TEXT_COLOR_BLUE + msg.getMessage());
            case LoadGameMessage msg ->
                    System.out.println(SET_TEXT_COLOR_BLUE + msg.getMessage());
            default ->
                    throw new IllegalStateException("Unexpected value: " + notification);
        }
    }

    public String eval(String input) {
        try {
            if(this.state == INGAME){
                return inGameHandler.eval(input);
            } else {
                String[] tokens = input.toLowerCase().split(" ");
                String cmd = (tokens.length > 0) ? tokens[0] : "help";
                String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
                return switch (cmd) {
                    case "create" -> createGame(params);
                    case "list" -> listGames();
                    case "join" -> joinGame(params);
                    case "observe" -> observe(params);
                    case "logout" -> logout();
                    case "clear" -> clear();
                    case "quit" -> "quit";
                    default -> help();
                };
            }
        } catch (Exception e) {
            return preHandler.clientExceptionHandler(e);
        }
    }

    public String logout() throws Exception {
        try {
            String token = preHandler.currentUser.authToken();
            LogoutResult logoutResult = server.logout(token);
            preHandler.state = SIGNEDOUT;
            return SET_TEXT_COLOR_YELLOW + "Successfully logged out: " + preHandler.currentUser.username();
        } catch (Exception e){
            return preHandler.clientExceptionHandler(e);
        }
    }

    public String createGame(String... params) throws Exception {
        try {
            if(params.length == 1){
                String token = preHandler.currentUser.authToken();
                NewGameResult createGameResult = server.createGame(new NewGameRequest(params[0]), token);
                return SET_TEXT_COLOR_YELLOW + "Successfully created game: " + params[0];
            } else {
                return "Enter valid game creation info -> \"create\" <GAMENAME>";
            }
        } catch (Exception e){
            return preHandler.clientExceptionHandler(e);
        }
    }

    public String joinGame(String... params) throws Exception {
        try {
            if(params.length == 2){
                String token = preHandler.currentUser.authToken();
                JoinGameRequest request = new JoinGameRequest(params[1].toUpperCase(), Integer.parseInt(params[0]));
                JoinGameResult joinGameResult = server.joinGame(request, token);
                ListGamesResult listGamesResult = server.listGames(token);
                printChessBoard(params[1].toLowerCase());
                return SET_TEXT_COLOR_YELLOW + "Successfully joined game";
            } else {
                return "Enter valid join request -> \"join\" <ID> [WHITE|BLACK]";
            }
        } catch (NumberFormatException e) {
            return "Enter valid join request -> \"join\" <ID> [WHITE|BLACK]";
        } catch (Exception e) {
            return preHandler.clientExceptionHandler(e);
        }

    }

    public String observe(String... params) throws Exception {
        try {
            if (params.length == 1){
                String token = preHandler.currentUser.authToken();
                ListGamesResult listGamesResult = server.listGames(token);
                for (GameData game : listGamesResult.games()) {
                    if(game.gameID() == Integer.parseInt(params[0])) {
                        printChessBoard("white");
                        return "";
                    }
                }
                return "Enter valid observe request -> \"observe\" <ID>";
            } else {
                return "Enter valid observe request -> \"observe\" <ID>";
            }
        } catch (Exception e) {
            return preHandler.clientExceptionHandler(e);
        }
    }

    public void printChessBoard(String color) {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        ChessBoardRender.render(board, color);
    }

    public String listGames() throws Exception {
        try {
            String token = preHandler.currentUser.authToken();
            ListGamesResult listGamesResult = server.listGames(token);
            String resultString = "";
            for(GameData game : listGamesResult.games()){
                resultString += game.gameID() + ". Game name: " +  game.gameName() +
                        "       White: " + game.whiteUsername() +
                        "       Black: " + game.blackUsername() + "\n";
            }
            if(!resultString.isEmpty()){
                return resultString;
            } else {
                return "No games created yet";
            }
        } catch (Exception e) {
            return preHandler.clientExceptionHandler(e);
        }
    }

    public String clear() throws Exception{
        preHandler.state = SIGNEDOUT;
        return preHandler.clear();
    }

    public String help() {
        return """
                Options:
                    "create" <NAME> - a game
                    "list" - games
                    "join" <ID> [WHITE|BLACK] - a game
                    "observe" <ID> - a game
                    "logout" - when you are done
                    "quit" - playing chess
                    "help" - with possible commands
                """;
    }
}
