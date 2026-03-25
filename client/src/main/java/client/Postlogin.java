package client;

import chess.ChessBoard;
import model.GameData;
import model.request.*;
import model.result.*;
import server.ServerFacade;
import ui.ChessBoardRender;

import java.util.Arrays;
import java.util.List;

import static client.State.*;
import static ui.EscapeSequences.*;

public class Postlogin {
    private final ServerFacade server;
    private final String serverUrl;
    private Prelogin preHandler;

    public Postlogin(String serverUrl, ServerFacade server, Prelogin preHandler) {
        this.server = server;
        this.serverUrl = serverUrl;
        this.preHandler = preHandler;
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
//                case "observe" -> observe(params);
                case "logout" -> logout();
                case "clear" -> clear();
                case "quit" -> "quit";
                default -> help();
            };
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
                ChessBoard board = new ChessBoard();
                board.resetBoard();
                ChessBoardRender.render(board, params[1].toLowerCase());
                return SET_TEXT_COLOR_YELLOW + "Successfully joined game";
            } else {
                return "Enter valid join request -> \"join\" <ID> [WHITE|BLACK]";
            }
        } catch (Exception e) {
            return preHandler.clientExceptionHandler(e);
        }

    }

    public String listGames() throws Exception {
        try {
            String token = preHandler.currentUser.authToken();
            ListGamesResult listGamesResult = server.listGames(token);
            int i = 1;
            String resultString = "";
            for(GameData game : listGamesResult.games()){
                resultString += i + ". Game name: " +  game.gameName() +
                        "       White: " + game.whiteUsername() +
                        "       Black: " + game.blackUsername() + "\n";
                i += 1;
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
