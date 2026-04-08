package websocket.commands;

public class ConnectMessage extends UserGameCommand{
    public ConnectMessage(CommandType commandType, String authToken, Integer gameID) {
        super(commandType, authToken, gameID);
    }


}
