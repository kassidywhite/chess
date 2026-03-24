package client;

import java.util.Arrays;
import java.util.Scanner;

import com.google.gson.Gson;
import model.*;
import exception.ResponseException;
import server.ServerFacade;

import static ui.EscapeSequences.*;

public class Prelogin {

    public Prelogin(String serverUrl) {
        // create client here
    }

    public void run() {
        System.out.println("👑 Welcome to 240 Chess. Type Help to get started. 👑");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.println("something");
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
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
        return null;
    }

    public String login(String... params) throws Exception {
        return null;
    }

    private void printPrompt() {
        System.out.print("\n" + ERASE_LINE + ">>>");
    }

    public String help() {
        return null;
    }
}

