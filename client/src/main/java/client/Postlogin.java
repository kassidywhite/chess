package client;

import dataaccess.DataAccessException;
import model.result.LogoutResult;
import server.ServerFacade;
import service.exceptions.*;

import java.util.Arrays;

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
//                case "create" -> createGame(params);
//                case "list" -> listGames(params);
//                case "join" -> joinGame(params);
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

//    public String createGame() throws Exception {
//
//    }

    public String clear() throws Exception{
        preHandler.state = SIGNEDOUT;
        return preHandler.clear();
    }

    public String help() {
        return """
                Options:
                    create <NAME> - a game
                    list - games
                    join <ID> [WHITE|BLACK] - a game
                    observe <ID> - a game
                    logout - when you are done
                    quit - playing chess
                    help - with possible commands
                """;
    }
}
