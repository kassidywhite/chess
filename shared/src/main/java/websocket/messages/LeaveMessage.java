package websocket.commands;

public class LeaveMessage extends UserGameCommand {
    public LeaveMessage(CommandType commandType, String authToken, Integer gameID) {
        super(commandType, authToken, gameID);
    }
}
