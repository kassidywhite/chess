package client;

import java.util.Scanner;

public class Repl {

    public Repl(String serverUrl) {
        // create client here
    }

    public void run() {
        // print the welcome message

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
        }
    }
}
