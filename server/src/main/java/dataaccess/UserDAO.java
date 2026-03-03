package dataaccess;

import model.*;
import model.result.DeleteResult;

import java.util.ArrayList;
//import exception.ResponseException;

public interface UserDAO {

    void addUser(UserData data);

    ArrayList<UserData> listUsers();
    UserData getUser(String username);

    void deleteAllUsers();
}
