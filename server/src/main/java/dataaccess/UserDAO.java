package dataaccess;

import model.*;
import model.result.DeleteResult;

import java.util.ArrayList;
//import exception.ResponseException;

public interface UserDAO {

    void addUser(UserData data) throws DataAccessException;

    ArrayList<UserData> listUsers() throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;

    void deleteAllUsers() throws DataAccessException;
}
