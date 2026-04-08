package websocket.messages;

public class ErrorMessage extends ServerMessage{
    public String errorMessage;

    public ErrorMessage(ServerMessageType type, String message) {
        super(type);
        this.errorMessage = message;
    }

    public String getMessage() {
        return errorMessage;
    }
}
