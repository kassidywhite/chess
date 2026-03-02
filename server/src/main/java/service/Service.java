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
        validAuth(token);
        authAccess.deleteAuth(token);
        userAccess.deleteUser(auth.username());
        return new LogoutResult();
    }

    public NewGameResult createGame(NewGameRequest request, String authToken) throws ServiceException {
        validAuth(authToken);
        if(request.gameName() == null) {
            throw new BadRequestException("Error: bad request");
        }
        if(gameAccess.getGameByName(request.gameName()) != null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        GameData game = new GameData(null, null, request.gameName(), new ChessGame());
        gameAccess.addGame(game);
        return new NewGameResult(123);
    }

    public ListGamesResult listGames(String token) throws ServiceException {
        validAuth(token);
        List<GameData> games = gameAccess.listGames();
        return new ListGamesResult(games);
    }

    public JoinGameResult joinGame(JoinGameRequest request, String token) throws ServiceException {
        validAuth(token);
        GameData curr_game = gameAccess.getGameByID(request.gameID());
        if(curr_game == null) {
            throw new BadRequestException("Error: bad request");
        }
        if(Objects.equals(request.playerColor(), "WHITE")) {
            if(curr_game.whiteUsername() == null) {
                AuthData curr_user = authAccess.getAuthByToken(token);
                GameData new_game = new GameData(curr_user.username(), curr_game.blackUsername(), curr_game.gameName(), curr_game.game());
                gameAccess.deleteGame(curr_game.gameName());
                gameAccess.addGame(new_game);
            }
        } else if(Objects.equals(request.playerColor(), "BLACK")) {
            if(curr_game.blackUsername() == null) {
                AuthData curr_user = authAccess.getAuthByToken(token);
                GameData new_game = new GameData(curr_game.whiteUsername(), curr_user.username(), curr_game.gameName(), curr_game.game());
                gameAccess.deleteGame(curr_game.gameName());
                gameAccess.addGame(new_game);
            }
        } else {
            throw new AlreadyTakenException("Error: already taken");
        }
        return new JoinGameResult();
    }

    private void validAuth(String token) throws ServiceException{
        AuthData auth = authAccess.getAuthByToken(token);
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
