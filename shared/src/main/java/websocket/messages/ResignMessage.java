package websocket.commands;

public class ResignMessage extends UserGameCommand {
    public ResignMessage(CommandType commandType, String authToken, Integer gameID) {
        super(commandType, authToken, gameID);
    }
}
