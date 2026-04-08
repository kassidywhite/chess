package websocket.messages;

import websocket.commands.UserGameCommand;

public class MakeMoveMessage extends UserGameCommand {
    public MakeMoveMessage(CommandType commandType, String authToken, Integer gameID) {
        super(commandType, authToken, gameID);
    }
}
