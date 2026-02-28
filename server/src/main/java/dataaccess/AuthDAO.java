package dataaccess;

import model.*;
import model.result.DeleteResult;
import model.result.RegisterResult;

import java.util.ArrayList;

public interface AuthDAO {
    void AuthDAO();

    RegisterResult addAuth(AuthData data);

    ArrayList<AuthData> listAuth();

    AuthData getAuth(String token);

    DeleteResult deleteAuth(String token);
    DeleteResult deleteAllAuth();
}
