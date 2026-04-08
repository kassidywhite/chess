package websocket.messages;

import websocket.commands.UserGameCommand;

public class ResignMessage extends UserGameCommand {
    public ResignMessage(CommandType commandType, String authToken, Integer gameID) {
        super(commandType, authToken, gameID);
    }
}
