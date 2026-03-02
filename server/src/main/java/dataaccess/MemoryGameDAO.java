package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<GameData> listGames() {
        List<GameData> gameList = new ArrayList<>();
        for(Map.Entry<String, GameData> entry : games.entrySet()) {
            gameList.add(entry.getValue());
        }
        return gameList;
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
