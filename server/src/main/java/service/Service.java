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
import org.mindrot.jbcrypt.BCrypt;
import service.exceptions.AlreadyTakenException;
import service.exceptions.BadRequestException;
import service.exceptions.ServiceException;
import service.exceptions.UnauthorizedException;

import javax.xml.crypto.Data;
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
        try{
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
            return new RegisterResult(user.username(), authToken.authToken());
        } catch (DataAccessException e) {
            throw new ServiceException("Internal Service Error");
        }
    }

    public LoginResult login(LoginRequest request) throws ServiceException {
        try{
            if(request.username() == null || request.password() == null){
                throw new BadRequestException("Error: bad request");
            }
            UserData user = userAccess.getUser(request.username());
            if(user == null){
                throw new UnauthorizedException("Error: unauthorized");
            }
            if(userAccess instanceof SQLUserDAO && authAccess instanceof SQLAuthDAO && gameAccess instanceof SQLGameDAO){
                if(!BCrypt.checkpw(request.password(), user.password())){
                    throw new UnauthorizedException("Error: unauthorized");
                }
            } else {
                if(!request.password().equals(user.password())){
                    throw new UnauthorizedException("Error: unauthorized");
                }
            }
            AuthData authToken = new AuthData(user.username());
            authAccess.addAuth(authToken);
            return new LoginResult(user.username(), authToken.authToken());
        } catch (DataAccessException e) {
            throw new ServiceException("Internal Service Error");
        }
    }

    public LogoutResult logout(String token) throws ServiceException{
        try {
            validAuth(token);
            authAccess.deleteAuth(token);
            return new LogoutResult();
        } catch (DataAccessException e) {
            throw new ServiceException("Internal Service Error");
        }
    }

    public NewGameResult createGame(NewGameRequest request, String authToken) throws ServiceException {
        try {
            validAuth(authToken);
            if(request.gameName() == null) {
                throw new BadRequestException("Error: bad request");
            }
            if(gameAccess.getGameByName(request.gameName()) != null) {
                throw new AlreadyTakenException("Error: already taken");
            }
            return new NewGameResult(gameAccess.createNewGame(request.gameName()));
        } catch (DataAccessException e) {
            throw new ServiceException("Internal Service Error");
        }
    }

    public ListGamesResult listGames(String token) throws ServiceException {
        try {
            validAuth(token);
            List<GameData> games = gameAccess.listGames();
            return new ListGamesResult(games);
        } catch (DataAccessException e) {
            throw new ServiceException("Internal Service Error");
        }
    }

    public JoinGameResult joinGame(JoinGameRequest request, String token) throws ServiceException {
        try {
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
        } catch (DataAccessException e) {
            throw new ServiceException("Internal Service Error");
        }
    }

    private void validAuth(String token) throws ServiceException{
        try {
            String auth = authAccess.getUserByAuth(token);
            if(auth == null){
                throw new UnauthorizedException("Error: unauthorized");
            }
        } catch (DataAccessException e) {
            throw new ServiceException("Internal Service Error");
        }
    }

    public void addUser(UserData data) throws ServiceException {
        try {
            userAccess.addUser(data);
        } catch (DataAccessException e){
            throw new ServiceException("Internal Service Error");
        }
    }

    public DeleteResult deleteAll(String token) throws ServiceException {
        // check if authToken is valid
        try {
            userAccess.deleteAllUsers();
            authAccess.deleteAllAuth();
            gameAccess.deleteAllGames();
            return new DeleteResult();
        } catch (DataAccessException e){
            throw new ServiceException("Internal Service Error");
        }
    }

    public Collection<UserData> listUsers() throws ServiceException {
        try {
            return userAccess.listUsers();
        } catch (DataAccessException e) {
            throw new ServiceException("Internal Service Error");
        }
    }

}
