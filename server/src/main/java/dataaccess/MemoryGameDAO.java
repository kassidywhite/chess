package dataaccess;

import model.GameData;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    final private HashMap<String, GameData> games = new HashMap<>();

    @Override
    public void GameDAO() {

    }

    @Override
    public void addGame(GameData game) {
        games.put(game.gameName(), game);
    }

    @Override
    public GameData getGame(String gameName) {
        if(games.containsKey(gameName)){
            return games.get(gameName);
        }
        return null;
    }

    @Override
    public void deleteGame(String gameName) {
        games.remove(gameName);
    }

    @Override
    public void deleteAllGames() {
        games.clear();
    }
}
