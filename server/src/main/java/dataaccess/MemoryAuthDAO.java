package dataaccess;
import model.*;
import model.result.DeleteResult;
import model.result.RegisterResult;

import java.util.ArrayList;
import java.util.HashMap;

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
        if(tokens.containsValue(token)){
            return new AuthData(tokens.get(token).username(), token);
        } else {
            return null;
        }
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
