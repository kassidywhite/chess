package model;
import chess.ChessGame;

public record GameData (
        int gameID,
        String whiteUsername,
        String blackUsername,
        String gameName,
        ChessGame game
) {
    static int i = 0;

    public GameData(String name) {
        this(generateInt(), null, null, name, createGame());
    }

    public static int generateInt() {
        i += 1;
        return i;
    }

    public static ChessGame createGame() {
        return new ChessGame();
    }
}
