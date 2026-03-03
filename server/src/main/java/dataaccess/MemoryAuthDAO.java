package dataaccess;
import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {
    final private HashMap<String, String> tokens = new HashMap<>();

    @Override
    public void addAuth(AuthData data) {
        tokens.put(data.authToken(), data.username());
    }


    public String getAuthByUser(String username) {
        for(Map.Entry<String, String> entry: tokens.entrySet()){
            if(entry.getValue().equals(username)){
                return entry.getKey();
            }
        }
        return null;
    }

    public String getUserByAuth(String token) {
        if(tokens.containsKey(token)){
            return tokens.get(token);
        }
        return null;
    }

    @Override
    public void deleteAuth(String token) {
        tokens.remove(token);
    }

    @Override
    public void deleteAllAuth() {
        tokens.clear();
    }
}
