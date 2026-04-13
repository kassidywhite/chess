package server.websocket;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import server.Server;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    public final ConcurrentHashMap<Integer, ArrayList<Session>> connections = new ConcurrentHashMap<>();

    public void add(int gameId, Session session){
        connections.compute(gameId, (k, v) -> {
            if (v==null) {
                v = new ArrayList<>();
            }
            v.add(session);
            return v;
        });
    }

    public void removeSession(int gameId, Session session){
        connections.get(gameId).remove(session);
    }

    // method that sends message to every player in the game
    public void broadcast(Session excludeSession, ServerMessage message, int gameId) throws IOException {
        String msg = new Gson().toJson(message);
        for (Session c : connections.get(gameId)) {
            if (c.isOpen()){
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }

    // create method for root client to get message
    public void rootMessage(Session currSession, ServerMessage message) throws IOException {
        String msg = new Gson().toJson(message);
        if (currSession.isOpen()){
            currSession.getRemote().sendString(msg);
        }
    }
}
