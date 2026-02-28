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


    @Override
    public AuthData getAuth(String username) {
        if(tokens.containsKey(username)){
            return tokens.get(username);
        }
        return null;
    }

    @Override
    public DeleteResult deleteAuth(String token) {
        tokens.remove(token);
        return new DeleteResult();
    }

    @Override
    public DeleteResult deleteAllAuth() {
        tokens.clear();
        return new DeleteResult();
    }
}
