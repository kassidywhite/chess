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
import java.util.Map;

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
            Collection<ChessMove> possibilities = game.validMoves(currPosition);

            boolean whiteUserCheck = moveThis.getTeamColor() == ChessGame.TeamColor.WHITE && gameData.whiteUsername().equals(username);
            boolean blackUserCheck = moveThis.getTeamColor() == ChessGame.TeamColor.BLACK && gameData.blackUsername().equals(username);

            if (game.getTeamTurn() != moveThis.getTeamColor()) { // check if it's the users turn
                var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Other player hasn't joined/moved yet!");
                connections.rootMessage(session, errorMessage);
            } else if (game.gameOver){
                var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Game is over");
                connections.rootMessage(session, errorMessage);
            } else if(possibilities.isEmpty() | !possibilities.contains(move)){ // check if the move is valid
                var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Invalid move");
                connections.rootMessage(session, errorMessage);
            } else {
                if (!(whiteUserCheck | blackUserCheck)) { // checks if the player is the right color or if it's an observer
                    var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Unauthorized to move piece");
                    connections.rootMessage(session, errorMessage);
                } else {
                    try {
                        game.makeMove(move);
                    } catch (InvalidMoveException ex){
                        var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Invalid move");
                        connections.rootMessage(session, errorMessage);
                        return;
                    }
                    game.isInCheckmate(game.getTeamTurn());
                    game.boardCopy = game.getBoard().clone();
                    GameData newGD = new GameData(
                            gameData.gameID(),
                            gameData.whiteUsername(),
                            gameData.blackUsername(),
                            gameData.gameName(),
                            game
                    );
                    updateGame(newGD, game);

                    var loadMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, game);
                    connections.rootMessage(session, loadMessage);
                    connections.broadcast(session, loadMessage, command.getGameID());
                    var msg = "";
                    System.out.println("Team turn: " + game.getTeamTurn().toString());
                    System.out.println(move.getEndPosition());
                    System.out.println(move.getStartPosition());
                    msg = username + " moved " + moveThis.getPieceType().toString().toLowerCase() + " to "
                            + findCorrespondingNumber(move.getEndPosition().getColumn()) + (move.getEndPosition().getRow());

                    var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
                    connections.broadcast(session, notification, command.getGameID());

                    ChessGame.TeamColor teamColor = whiteUserCheck ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
                    ChessGame.TeamColor oppColor = whiteUserCheck ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
                    String oppName = gameData.whiteUsername().equals(username) ? gameData.blackUsername() : gameData.whiteUsername();

                    System.out.println("Game is over? -> " + game.gameOver);
                    if (game.gameOver) {
                        var mess = String.format("%s is in checkmate!", oppName);
                        var not = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, mess);
                        connections.broadcast(session, not, command.getGameID());
                        connections.rootMessage(session, not);
                    }

                    if (game.isInCheck(oppColor) && !game.gameOver){
                        var mess = String.format("%s is in check!", oppName);
                        var not = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, mess);
                        connections.broadcast(session, not, command.getGameID());
                        connections.rootMessage(session, not);
                    }
                }
            }
        }
    }

    private String findCorrespondingNumber(int num) {
        Map<Integer, String> numVals = Map.of(
                8, "h",
                7, "g",
                6, "f",
                5, "e",
                4, "d",
                3, "c",
                2, "b",
                1, "a"
        );
        return numVals.get(num);
    }

    private void leaveGame(Session session, String username, UserGameCommand command) throws IOException, DataAccessException {
        GameData gameData = server.getGame(command.getGameID());
        boolean whiteUserCheck = gameData.whiteUsername() != null && gameData.whiteUsername().equals(username);
        boolean blackUserCheck = gameData.blackUsername() != null && gameData.blackUsername().equals(username);

        if(server.getGame(command.getGameID()) != null) {
            if (whiteUserCheck | blackUserCheck) {
                GameData newGD;
                if (whiteUserCheck) {
                    newGD = new GameData(
                            command.getGameID(),
                            null,
                            gameData.blackUsername(),
                            gameData.gameName(),
                            gameData.game()
                    );
                    updateGame(newGD, newGD.game());
                } else {
                    newGD = new GameData(
                            command.getGameID(),
                            gameData.whiteUsername(),
                            null,
                            gameData.gameName(),
                            gameData.game()
                    );
                    updateGame(newGD, newGD.game());
                }

            }
            connections.removeSession(command.getGameID(), session);
            var message = String.format("%s has left the game", username);
            var othersNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(session, othersNotification, command.getGameID());
        } else {
                var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Invalid GameID");
                connections.rootMessage(session, errorMessage);
            }

    }

    private void resign(Session session, String username, UserGameCommand command) throws DataAccessException, IOException {
        GameData gameData = server.getGame(command.getGameID());
        ChessGame game = gameData.game();

        boolean whiteUserCheck = username.equals(gameData.whiteUsername());
        boolean blackUserCheck = username.equals(gameData.blackUsername());

        if (whiteUserCheck | blackUserCheck){ // check if valid user
            if (!game.gameOver){ // check if game is not over
                game.gameOver = true;
                var msg = String.format("%s has resigned. The game is over.", username);
                var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
                connections.broadcast(session, notification, command.getGameID());
                var rootNot = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "You have resigned");
                connections.rootMessage(session, rootNot);
                GameData newGD = new GameData(
                        gameData.gameID(),
                        gameData.whiteUsername(),
                        gameData.blackUsername(),
                        gameData.gameName(),
                        game
                );
                updateGame(newGD, game);
            } else {
                var msg = "Game already finished.";
                var errorNot = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, msg);
                connections.rootMessage(session, errorNot);
            }
        } else {
            var errnot = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Unauthorized");
            connections.rootMessage(session, errnot);
        }
    }

    private void updateGame(GameData gameData, ChessGame newGame) throws DataAccessException {
        GameData newGD = new GameData(
                gameData.gameID(),
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                newGame
        );
        server.updateGame(newGD);
    }

    private String getUsername(String authToken) throws DataAccessException {
        return server.getUserByAuth(authToken);
    }
}