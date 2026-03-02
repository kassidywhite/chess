package model;
import chess.ChessGame;
import java.util.Random;

public record GameData (
        int gameID,
        String whiteUsername,
        String blackUsername,
        String gameName,
        ChessGame game
) {

    public GameData(String whiteUser, String blackUser, String name, ChessGame game) {
        this(generateInt(), whiteUser, blackUser, name, game);
    }

    public static int generateInt() {
        Random random = new Random();
        return random.nextInt(1000);
    }

    public static ChessGame createGame() {
        return new ChessGame();
    }

}
