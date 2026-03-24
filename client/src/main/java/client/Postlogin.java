package client;

import server.ServerFacade;

public class Postlogin {
    private State state;
    private final ServerFacade server;

    public Postlogin(ServerFacade server, State state) {
        this.server = server;
        this.state = state;
    }

    public String eval(String input) {
        return null;
    }
}
