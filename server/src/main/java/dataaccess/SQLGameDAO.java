package dataaccess;

import model.GameData;

import java.util.List;

public class SQLGameDAO implements GameDAO {
    @Override
    public int createNewGame(String gameName) {
        return 0;
    }

    @Override
    public void addGame(GameData game) {

    }

    @Override
    public GameData getGameByName(String gameName) {
        return null;
    }

    @Override
    public GameData getGameByID(int id) {
        return null;
    }

    @Override
    public List<GameData> listGames() {
        return List.of();
    }

    @Override
    public void deleteGame(String gameName) {

    }

    @Override
    public void deleteAllGames() {

    }
}
