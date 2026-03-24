package client;

import server.ServerFacade;

import java.util.Arrays;

public class Postlogin {
    private State state;
    private final ServerFacade server;
    private final String serverUrl;

    public Postlogin(String serverUrl, ServerFacade server, State state) {
        this.server = server;
        this.state = state;
        this.serverUrl = serverUrl;
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
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
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
