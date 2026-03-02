package dataaccess;
import model.*;
import model.result.DeleteResult;
import model.result.RegisterResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {
    final private HashMap<String, AuthData> tokens = new HashMap<>();

    @Override
    public void AuthDAO() {

    }

    @Override
    public void addAuth(AuthData data) {
        tokens.put(data.username(), data);
    }

    @Override
    public ArrayList<AuthData> listAuth() {
        return new ArrayList<>(tokens.values());
    }


    public AuthData getAuthByUser(String username) {
        if(tokens.containsKey(username)){
            return tokens.get(username);
        }
        return null;
    }

    public AuthData getAuthByToken(String token) {
        for(Map.Entry<String, AuthData> entry : tokens.entrySet()) {
            if(entry.getValue().getToken().equals(token)){
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public void deleteAuth(String token) {
        tokens.remove(getAuthByToken(token).username());
    }

    @Override
    public void deleteAllAuth() {
        tokens.clear();
    }
}
