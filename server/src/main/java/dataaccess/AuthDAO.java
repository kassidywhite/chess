package dataaccess;

import model.*;
import model.result.DeleteResult;
import model.result.RegisterResult;

import java.util.ArrayList;

public interface AuthDAO {

    void addAuth(AuthData data) throws DataAccessException;

    String getAuthByUser(String username) throws DataAccessException;
    String getUserByAuth(String token) throws DataAccessException;

    void deleteAuth(String token) throws DataAccessException;
    void deleteAllAuth() throws DataAccessException;
}
