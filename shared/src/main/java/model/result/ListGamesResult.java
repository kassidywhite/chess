package model.result;

import chess.ChessGame;
import model.GameData;

import java.util.List;

public record ListGamesResult (List<GameData> games) {
    public GameData getGame(int id){
        for (GameData game : games){
            if(game.gameID() == id){
                return game;
            }
        }
        return null;
    }
}
