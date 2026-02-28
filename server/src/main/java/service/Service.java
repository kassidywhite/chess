package service;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import dataaccess.*;
import model.result.DeleteResult;
import model.result.RegisterResult;

import java.util.ArrayList;
import java.util.Collection;

public class Service {

    private final UserDAO userAccess = new MemoryUserDAO();
    private final AuthDAO authAccess = new MemoryAuthDAO();
    private final GameDAO gameAccess = new MemoryGameDAO();

    public Service(){}

    public RegisterResult register(UserData user) throws DataAccessException{
        if(userAccess.getUser(user.username()) != null){
            throw new DataAccessException("Error: Username already taken");
        }
        AuthData authToken = new AuthData(user.username());
        userAccess.addUser(user);
        return authAccess.addAuth(authToken);
    }

    public void addUser(UserData data){
        userAccess.addUser(data);
    }

    public DeleteResult deleteAll(String token) throws DataAccessException {
        // check if authToken is valid
        userAccess.deleteAllUsers();
        authAccess.deleteAllAuth();
        //gameAccess.deleteAllGames();
        return userAccess.deleteAllUsers();
    }

    public Collection<UserData> listUsers(){
        return userAccess.listUsers();
    }

}
