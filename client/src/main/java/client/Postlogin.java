package client;

import server.ServerFacade;

import java.util.Arrays;

import static client.State.*;

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
//                case "logout" -> logout(params);
                case "clear" -> clear();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String clear() throws Exception{
        preHandler.state = SIGNEDOUT;
        return preHandler.clear();
    }

    public String help() {
        return """
                Try typing:
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
