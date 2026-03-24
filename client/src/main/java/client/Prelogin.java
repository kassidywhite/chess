package client;

import java.util.Arrays;
import java.util.Scanner;

import com.google.gson.Gson;
import model.*;
import exception.ResponseException;
import model.request.*;
import model.result.*;
import server.ServerFacade;

import static client.State.*;
import static ui.EscapeSequences.*;

public class Prelogin {
    private final ServerFacade server;
    private State state = SIGNEDOUT;
    private Postlogin postHandler;

    public Prelogin(String serverUrl) {
        server = new ServerFacade(serverUrl);
        postHandler = new Postlogin(serverUrl, server, state);
    }

    public void run() {
        System.out.println(SET_TEXT_COLOR_WHITE + "👑✨ Welcome to 240 Chess. Type Help to get started. ✨👑");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                if(!line.equals("quit")){
                    if(this.state == SIGNEDOUT){
                        result = eval(line);
                        System.out.println(SET_TEXT_COLOR_PINK + result + SET_TEXT_COLOR_WHITE);
                    } else {
                        result = postHandler.eval(line);
                        System.out.println(SET_TEXT_COLOR_PINK + result + SET_TEXT_COLOR_WHITE);
                    }
                } else {
                    result = eval(line);
                    System.out.println(SET_TEXT_COLOR_BLUE + "Quitting server... Thanks for playing!");
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String register(String... params) throws Exception {
        if (params.length == 3){
            state = State.SIGNEDIN;
            RegisterResult registerResult = server.register(new RegisterRequest(params[0], params[1], params[2]));
            return SET_TEXT_COLOR_YELLOW + "Successfully registered " + registerResult.username();
        } else {
            return "Enter valid registration info -> register <USERNAME <PASSWORD> <EMAIL>";
        }
    }

    public String login(String... params) throws Exception {
        return null;
    }

    private void printPrompt() {
        System.out.print("\n" + ERASE_LINE + ">>> ");
    }

    public String help() {
        return """
                Try typing:
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to play chess
                    quit - playing chess
                    help - with possible commands
                """;
    }
}

