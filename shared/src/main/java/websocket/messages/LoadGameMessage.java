package websocket.messages;

import chess.ChessBoard;
import chess.ChessGame;

public class LoadGameMessage extends ServerMessage{
    public String message;
    public ChessGame game;

    public LoadGameMessage(ServerMessageType type, String message, ChessGame game) {
        super(type);
        this.message = message;
        this.game = game;
    }

    public String getMessage() {
        return message;
    }

    public ChessGame getGame() {
        return game;
    }
}
