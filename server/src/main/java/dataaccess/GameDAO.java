package dataaccess;

import model.GameData;

import java.util.List;

public interface GameDAO {

    void GameDAO();

    int createNewGame(String gameName);
    void addGame(GameData game);

    GameData getGameByName(String gameName);
    GameData getGameByID(int ID);
    List<GameData> listGames();

    void deleteGame(String gameName);
    void deleteAllGames();
}

