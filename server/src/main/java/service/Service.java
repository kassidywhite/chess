package service;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import dataaccess.*;
import model.request.LoginRequest;
import model.request.RegisterRequest;
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

    public RegisterResult register(RegisterRequest request) throws ServiceException {
        if(request.username() == null || request.password() == null || request.email() == null){
            throw new BadRequestException("Error: bad request");
        }
        if(userAccess.getUser(request.username()) != null){
            throw new AlreadyTakenException("Error: already taken");
        }
        UserData user = new UserData(request.username(), request.password(), request.email());
        AuthData authToken = new AuthData(request.username());
        userAccess.addUser(user);
        authAccess.addAuth(authToken);
        authAccess.addAuth(authToken);
        RegisterResult result = new RegisterResult(user.username(), authToken.authToken());
        return result;
    }

    public LoginResult login(LoginRequest request) throws ServiceException {
        if(request.username() == null || request.password() == null){
            throw new BadRequestException("Error: bad request");
        }
        UserData user = userAccess.getUser(request.username());
        if(user == null){
            throw new UnauthorizedException("Error: unauthorized");
        }
        if(!request.password().equals(user.password())){
            throw new UnauthorizedException("Error: unauthorized");
        }
        AuthData authToken = new AuthData(user.username());
        authAccess.addAuth(authToken);
        return new LoginResult(user.username(), authToken.authToken());
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
