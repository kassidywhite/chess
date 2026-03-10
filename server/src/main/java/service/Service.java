package service;
import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import model.request.JoinGameRequest;
import model.request.LoginRequest;
import model.request.NewGameRequest;
import model.request.RegisterRequest;
import model.result.*;
import service.exceptions.AlreadyTakenException;
import service.exceptions.BadRequestException;
import service.exceptions.ServiceException;
import service.exceptions.UnauthorizedException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class Service {

    final UserDAO userAccess = new SQLUserDAO();
    final AuthDAO authAccess = new SQLAuthDAO();
    final GameDAO gameAccess = new SQLGameDAO();

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
        return new RegisterResult(user.username(), authToken.authToken());
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
        validAuth(token);
        authAccess.deleteAuth(token);
        return new LogoutResult();
    }

    public NewGameResult createGame(NewGameRequest request, String authToken) throws ServiceException {
        validAuth(authToken);
        if(request.gameName() == null) {
            throw new BadRequestException("Error: bad request");
        }
        if(gameAccess.getGameByName(request.gameName()) != null) {
            throw new AlreadyTakenException("Error: already taken");
        }
        return new NewGameResult(gameAccess.createNewGame(request.gameName()));
    }

    public ListGamesResult listGames(String token) throws ServiceException {
        validAuth(token);
        List<GameData> games = gameAccess.listGames();
        return new ListGamesResult(games);
    }

    public JoinGameResult joinGame(JoinGameRequest request, String token) throws ServiceException {
        validAuth(token);
        GameData currGame = gameAccess.getGameByID(request.gameID());
        if(currGame == null) {
            throw new BadRequestException("Error: bad request");
        }
        if(Objects.equals(request.playerColor(), "WHITE")) {
            if(currGame.whiteUsername() == null) {
                String currUser = authAccess.getUserByAuth(token);
                GameData newGame = new GameData(currGame.gameID(), currUser, currGame.blackUsername(), currGame.gameName(), currGame.game());
                gameAccess.deleteGame(currGame.gameName());
                gameAccess.addGame(newGame);
                return new JoinGameResult();
            } else {
                throw new UnauthorizedException("Error: unauthorized");
            }
        } else if(Objects.equals(request.playerColor(), "BLACK")) {
            if(currGame.blackUsername() == null) {
                String currUser = authAccess.getUserByAuth(token);
                GameData newGame = new GameData(currGame.gameID(), currGame.whiteUsername(), currUser, currGame.gameName(), currGame.game());
                gameAccess.deleteGame(currGame.gameName());
                gameAccess.addGame(newGame);
                return new JoinGameResult();
            }
        } else {
            throw new BadRequestException("Error: bad request");
        }
        throw new AlreadyTakenException("Error: already taken");
    }

    private void validAuth(String token) throws ServiceException{
        String auth = authAccess.getUserByAuth(token);
        if(auth == null){
            throw new UnauthorizedException("Error: unauthorized");
        }
    }

    public void addUser(UserData data){
        userAccess.addUser(data);
    }

    public DeleteResult deleteAll(String token) {
        // check if authToken is valid
        userAccess.deleteAllUsers();
        authAccess.deleteAllAuth();
        gameAccess.deleteAllGames();
        return new DeleteResult();
    }

    public Collection<UserData> listUsers(){
        return userAccess.listUsers();
    }

}
