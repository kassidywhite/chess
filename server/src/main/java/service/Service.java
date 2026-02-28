package service;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import dataaccess.*;
import model.request.LoginRequest;
import model.result.DeleteResult;
import model.result.LoginResult;
import model.result.RegisterResult;
import service.exceptions.AlreadyTakenException;
import service.exceptions.BadRequestException;
import service.exceptions.ServiceException;
import service.exceptions.UnauthorizedException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class Service {

    private final UserDAO userAccess = new MemoryUserDAO();
    private final AuthDAO authAccess = new MemoryAuthDAO();
    private final GameDAO gameAccess = new MemoryGameDAO();

    public Service(){}

    public RegisterResult register(UserData user) throws ServiceException {
        if(user.username() == null || user.password() == null || user.email() == null){
            throw new BadRequestException("Error: bad request");
        }
        if(userAccess.getUser(user.username()) != null){
            throw new AlreadyTakenException("Error: already taken");
        }
        AuthData authToken = new AuthData(user.username());
        userAccess.addUser(user);
        authAccess.addAuth(authToken);
        authAccess.addAuth(authToken);
        RegisterResult result = new RegisterResult(user.username(), authToken.authToken());
        return result;
    }

    public LoginResult login(LoginRequest request) throws ServiceException {
        UserData user = userAccess.getUser(request.username());
        if(user == null){
            throw new BadRequestException("Error: bad request");
        }
        if(!Objects.equals(request.password(), user.password())){
            throw new UnauthorizedException("Error: unauthorized");
        }
        AuthData authToken = new AuthData(user.username());
        authAccess.addAuth(authToken);
        LoginResult result = new LoginResult(user.username(), authToken.authToken());
        return result;
    }

    public void addUser(UserData data){
        userAccess.addUser(data);
    }

    public DeleteResult deleteAll(String token) {
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
