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
    public RegisterResult addAuth(AuthData data) {
        tokens.put(data.authToken(), data);
        return new RegisterResult(data.username(), data.authToken());
    }

    @Override
    public ArrayList<AuthData> listAuth() {
        return new ArrayList<>(tokens.values());
    }


    @Override
    public AuthData getAuth(String token) {
        if(tokens.containsKey(token)){
            return tokens.get(token);
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
