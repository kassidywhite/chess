package websocket.messages;

import websocket.commands.UserGameCommand;

public class ConnectMessage extends UserGameCommand {
    public ConnectMessage(CommandType commandType, String authToken, Integer gameID) {
        super(commandType, authToken, gameID);
    }


}
