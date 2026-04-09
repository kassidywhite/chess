package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.websocket.*;
import model.GameData;
import org.jetbrains.annotations.NotNull;
import org.eclipse.jetty.websocket.api.Session;
import server.Server;
import service.exceptions.ServiceException;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Collection;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final Server server;

    public WebSocketHandler(Server server) {
        this.server = server;
    }

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {
        System.out.println("Websocket closed");
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext wsConnectContext) throws Exception {
        System.out.println("Websocket connected");
        wsConnectContext.enableAutomaticPings();;
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext wsMessageContext) throws Exception {
        Session session = wsMessageContext.session;

        UserGameCommand command = new Gson().fromJson(
                wsMessageContext.message(), UserGameCommand.class);
        String username = getUsername(command.getAuthToken());

        if (command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE){
            MakeMoveCommand moveCommand = new Gson().fromJson(
                    wsMessageContext.message(), MakeMoveCommand.class);
            makeMove(session, username, command, moveCommand.getMove());
        } else {
            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, command);
                case LEAVE -> leaveGame(session, username, command);
                case RESIGN -> resign(session, username, command);
            }
        }
    }

    private void connect(Session session, String username, UserGameCommand command) throws IOException, ServiceException, DataAccessException {
        if (!server.confirmAuth(username)) { // check if user registered
            var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Didn't register");
            connections.rootMessage(session, errorMessage);
        } else if(server.getGame(command.getGameID()) == null){ // check if the gameId is valid
            var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Invalid GameID");
            connections.rootMessage(session, errorMessage);
        } else {
            connections.add(command.getGameID(), session);
            ChessGame game = server.getGame(command.getGameID()).game();
            var notification = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, game);
            connections.rootMessage(session, notification);
            var message = String.format("%s has entered the game", username);
            var othersNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(session, othersNotification, command.getGameID());
        }
    }

    private void makeMove(Session session, String username, UserGameCommand command, ChessMove move) throws ServiceException, IOException, InvalidMoveException, DataAccessException {
        if (!server.confirmAuth(username)) {
            var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Didn't register");
            connections.rootMessage(session, errorMessage);
        }
        else {
            GameData gameData = server.getGame(command.getGameID());
            ChessGame game = gameData.game();
            ChessPosition currPosition = new ChessPosition(move.getStartPosition().getRow(), move.getStartPosition().getColumn());
            ChessPiece moveThis = game.getBoard().getPiece(currPosition);
            Collection<ChessMove> possibilities = ChessPiece.pieceMoves(game.getBoard(), currPosition);

            boolean whiteUserCheck = moveThis.getTeamColor() == ChessGame.TeamColor.WHITE && gameData.whiteUsername().equals(username);
            boolean blackUserCheck = moveThis.getTeamColor() == ChessGame.TeamColor.BLACK && gameData.blackUsername().equals(username);

            if (!(whiteUserCheck | blackUserCheck)){ // checks if the player is the right color or if it's an observer
                var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Unauthorized to move piece");
                connections.rootMessage(session, errorMessage);
            } else if (game.getTeamTurn() != moveThis.getTeamColor()) { // check if it's the users turn
                var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Unauthorized to move piece");
                connections.rootMessage(session, errorMessage);
            } else if (game.isInCheckmate(moveThis.getTeamColor()) | game.isInStalemate(moveThis.getTeamColor())){
                var loadMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, game);
                connections.rootMessage(session, loadMessage);
                var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Game over");
                connections.rootMessage(session, notification);

            } else if(possibilities.isEmpty() | !possibilities.contains(move)){ // check if the move is valid
                var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Invalid move");
                connections.rootMessage(session, errorMessage);
            } else {
                game.makeMove(move);
                var loadMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, game);
                connections.rootMessage(session, loadMessage);
                connections.broadcast(session, loadMessage, command.getGameID());

                var msg = username + " moved " + moveThis.getPieceType().toString();
                var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
                connections.broadcast(session, notification, command.getGameID());

                GameData newGameData = new GameData(gameData.gameID(),
                        gameData.whiteUsername(),
                        gameData.blackUsername(),
                        gameData.gameName(),
                        game);
                server.updateGame(newGameData);
            }
        }
    }

    private void leaveGame(Session session, String username, UserGameCommand command) throws IOException, DataAccessException {
        if(server.getGame(command.getGameID()) == null){
            var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Invalid GameID");
            connections.rootMessage(session, errorMessage);
        } else {
            connections.removeSession(command.getGameID(), session);
            var message = String.format("%s has left the game", username);
            var othersNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(session, othersNotification, command.getGameID());
        }
    }

    private void resign(Session session, String username, UserGameCommand command){

    }

    private String getUsername(String authToken) throws DataAccessException {
        return server.getUserByAuth(authToken);
    }
}