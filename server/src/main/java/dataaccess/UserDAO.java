package dataaccess;

import model.*;
import model.result.DeleteResult;

import java.util.ArrayList;
//import exception.ResponseException;

public interface UserDAO {

    void UserDAO();
    //create
    void addUser(UserData data); //throws DataAccessException

    //read
    ArrayList<UserData> listUsers();
    UserData getUser(String username);

    //update

    //delete
    DeleteResult deleteUser(String username);
    DeleteResult deleteAllUsers();
}
