package client;

import java.util.Arrays;
import java.util.Scanner;

import com.sun.nio.sctp.HandlerResult;
import client.websocket.NotificationHandler;
import com.sun.nio.sctp.Notification;
import model.*;
import model.request.*;
import model.result.*;
import serverfacade.ServerFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import static client.State.*;
import static ui.EscapeSequences.*;

public class Prelogin implements NotificationHandler {
    private final ServerFacade server;
    public State state = SIGNEDOUT;
    private Postlogin postHandler;
    public AuthData currentUser;

    public Prelogin(String serverUrl) {
        server = new ServerFacade(serverUrl);
        postHandler = new Postlogin(serverUrl, server, this);
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
                    } else if (line.equals("login")){
                        result = "";
                    } else {
                        result = postHandler.eval(line);
                    }
                    System.out.println(SET_TEXT_COLOR_PINK + result + SET_TEXT_COLOR_WHITE);
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
                case "clear" -> clear();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception e) {
            return clientExceptionHandler(e);
        }
    }

    public String register(String... params) throws Exception {
        if (params.length == 3){
            RegisterResult registerResult = server.register(new RegisterRequest(params[0], params[1], params[2]));
            currentUser = new AuthData(registerResult.authToken(), registerResult.username());
            state = SIGNEDIN;
            return SET_TEXT_COLOR_YELLOW + "Successfully registered " + registerResult.username();
        } else {
            return "Enter valid registration info -> register <USERNAME> <PASSWORD> <EMAIL>";
        }
    }

    public String login(String... params) throws Exception {
        if (params.length == 2){
            try {
                LoginResult loginResult = server.login(new LoginRequest(params[0], params[1]));
                currentUser = new AuthData(loginResult.authToken(), loginResult.username());
                state = SIGNEDIN;
                return SET_TEXT_COLOR_YELLOW + "Successfully logged in as: " + loginResult.username();
            } catch (Exception e) {
                return clientExceptionHandler(e);
            }
        } else {
            return "Enter valid login info -> login <USERNAME> <PASSWORD>";
        }
    }

    public String clear() throws Exception {
        DeleteResult deleteResult = server.clearAll();
        return "Successfully cleared server";
    }

    private void printPrompt() {
        if(state == SIGNEDOUT){
            System.out.print("\n" + ERASE_LINE + "[LOGGED_OUT] >>> ");
        } else {
            System.out.print("\n" + ERASE_LINE + "[LOGGED_IN] >>> ");
        }
    }

    public String help() {
        return """
                Options:
                    "register" <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    "login" <USERNAME> <PASSWORD> - to play chess
                    "quit" - playing chess
                    "help" - with possible commands
                """;
    }

    public String clientExceptionHandler(Exception e){
        String msg = e.getMessage();

        if (msg != null && msg.contains("\"message\"")) {
            int start = msg.indexOf(":\"") + 2;
            int end = msg.lastIndexOf("\"");
            return msg.substring(start, end);
        }

        return msg;
    }
}

