package dataaccess;

import model.GameData;

import java.util.List;

public interface GameDAO {

    void GameDAO();

    void addGame(GameData game);

    GameData getGame(String gameName);
    List<GameData> listGames();

    void deleteGame(String gameName);
    void deleteAllGames();
}

