package dataaccess;

import model.GameData;

public interface GameDAO {

    void GameDAO();

    void addGame(GameData game);

    GameData getGame(String gameName);

    void deleteGame(String gameName);
    void deleteAllGames();
}

