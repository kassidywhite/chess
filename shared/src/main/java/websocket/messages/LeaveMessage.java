package websocket.messages;

import websocket.commands.UserGameCommand;

public class LeaveMessage extends UserGameCommand {
    public LeaveMessage(CommandType commandType, String authToken, Integer gameID) {
        super(commandType, authToken, gameID);
    }
}
