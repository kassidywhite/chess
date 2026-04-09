package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.List;

public interface GameDAO {

    int createNewGame(String gameName) throws DataAccessException;
    void addGame(GameData game) throws DataAccessException;

    GameData getGameByName(String gameName) throws DataAccessException;
    GameData getGameByID(int id) throws DataAccessException;
    List<GameData> listGames() throws DataAccessException;

    void deleteGame(String gameName) throws DataAccessException;
    void deleteAllGames();

    void updateGame(GameData gameData) throws DataAccessException;
}

