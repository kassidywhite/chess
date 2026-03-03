package dataaccess;

import model.*;
import model.result.DeleteResult;
import model.result.RegisterResult;

import java.util.ArrayList;

public interface AuthDAO {
    void AuthDAO();

    void addAuth(AuthData data);

    ArrayList<String> listAuth();

    String getAuthByUser(String username);
    String getUserByAuth(String token);

    void deleteAuth(String token);
    void deleteAllAuth();
}
