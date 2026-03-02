package service;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import model.request.LoginRequest;
import model.request.NewGameRequest;
import model.request.RegisterRequest;
import model.result.*;
import service.exceptions.AlreadyTakenException;
import service.exceptions.BadRequestException;
import service.exceptions.ServiceException;
import service.exceptions.UnauthorizedException;

import java.util.Collection;

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

    public LogoutResult logout(String token) throws ServiceException{
        AuthData auth = authAccess.getAuthByToken(token);
        if(auth == null){
            throw new UnauthorizedException("Error: unauthorized");
        }
        authAccess.deleteAuth(token);
        userAccess.deleteUser(auth.username());
        return new LogoutResult();
    }

    public NewGameResult createGame(NewGameRequest request) {
        // check authorization
        // check if game already exists
        return new NewGameResult(123);
    }

    public void addUser(UserData data){
        userAccess.addUser(data);
    }

    public DeleteResult deleteAll(String token) {
        // check if authToken is valid
        userAccess.deleteAllUsers();
        authAccess.deleteAllAuth();
        //gameAccess.deleteAllGames();
        return new DeleteResult();
    }

    public Collection<UserData> listUsers(){
        return userAccess.listUsers();
    }

}
