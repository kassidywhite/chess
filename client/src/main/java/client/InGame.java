package client;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.PieceMovesCalculator;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import model.GameData;
import serverfacade.ServerFacade;
import websocket.messages.*;

import java.util.*;

import static ui.EscapeSequences.*;

public class InGame implements NotificationHandler {
    private final ServerFacade server;
    private final String serverUrl;
    private final Prelogin preHandler;
    private final WebSocketFacade ws;
    public State state = State.INGAME;
    Postlogin postHandler;

    public InGame(String serverUrl, ServerFacade server, Prelogin preHandler, Postlogin postHandler, WebSocketFacade ws) {
        this.server = server;
        this.serverUrl = serverUrl;
        this.preHandler = preHandler;
        this.postHandler = postHandler;
        this.ws = ws;
    }

    @Override
    public void notify(ServerMessage notification) {
        String teamColor = getTeamColor();
        switch (notification) {
            case LoadGameMessage msg -> loadGameNotification(msg);
            case NotificationMessage msg -> System.out.println(msg.getMessage());
            case ErrorMessage msg -> System.out.println(msg.getMessage());
            default ->
                System.out.println("oops something went wrong");
        }
    }

    public void loadGameNotification(LoadGameMessage msg) {
        GameData activeG = postHandler.activeGame;
        GameData newGD = new GameData(
                activeG.gameID(),
                activeG.whiteUsername(),
                activeG.blackUsername(),
                activeG.gameName(),
                msg.getGame()
        );
        postHandler.activeGame = newGD;
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "redraw" -> redraw();
                case "leave" -> leave();
                case "make-move" -> makeMove(params);
                case "resign" -> resign();
                case "highlight" -> highlight(params);
                default -> help();
            };
        } catch (Exception e) {
            return preHandler.clientExceptionHandler(e);
        }
    }

    public String redraw() {
        String teamColor = getTeamColor();
        if(teamColor == "black") {
            ChessBoardRender.render(postHandler.activeGame.game().getBoard(), Collections.EMPTY_LIST, "black");
        } else {
            ChessBoardRender.render(postHandler.activeGame.game().getBoard(), Collections.EMPTY_LIST, "white");
        }
        return "";
    }

    public String leave() {
        String token = preHandler.currentUser.authToken();
        GameData activeGame = postHandler.activeGame;
        state = State.SIGNEDIN;
        preHandler.state = State.SIGNEDIN;
        postHandler.state = State.SIGNEDIN;

        try {
            ws.leaveGame(token, activeGame.gameID());
        } catch (Exception e) {
            preHandler.clientExceptionHandler(e);
        }
        return "";
    }

    public String makeMove(String... params) {
        String token = preHandler.currentUser.authToken();
        // check for index out of bounds
        if(params.length == 2){
            List<String> splitParams = new ArrayList<>();
            splitParams.add(String.valueOf(params[0].charAt(0)));
            splitParams.add(String.valueOf(params[0].charAt(1)));
            splitParams.add(String.valueOf(params[1].charAt(0)));
            splitParams.add(String.valueOf(params[1].charAt(1)));

            if(findCorrespondingLetter(splitParams.get(0), getTeamColor()) != 0 && findCorrespondingLetter(splitParams.get(2), getTeamColor()) != 0 ){
                ChessPosition startPos = new ChessPosition(Integer.parseInt(splitParams.get(1)), findCorrespondingLetter(splitParams.get(0), getTeamColor()));
                ChessPosition endPos = new ChessPosition(Integer.parseInt(splitParams.get(3)), findCorrespondingLetter(splitParams.get(2), getTeamColor()));
                GameData activeGame = postHandler.activeGame;

                // check if it's a promotion piece
                ChessMove move = new ChessMove(startPos, endPos, null);
                try {
                    ws.makeMove(token, activeGame.gameID(), move);
                } catch (Exception e) {
                    preHandler.clientExceptionHandler(e);
                }
            }
        } else {
            return "Enter valid move request (row, col of piece to row, col of position) -> make-move <row><column> <row><column>";
        }
        return "";
    }

    public String resign() {
        String token = preHandler.currentUser.authToken();
        try {
            ws.resign(token, postHandler.activeGame.gameID());
            postHandler.activeGame = null;
            state = State.SIGNEDIN;
            preHandler.state = State.SIGNEDIN;
            postHandler.state = State.SIGNEDIN;
        } catch (Exception e) {
            preHandler.clientExceptionHandler(e);
        }
        return "";
    }

    public String highlight(String... params) {
        String teamColor = getTeamColor();
        if(params.length == 1){
            List<String> splitParams = new ArrayList<>();
            splitParams.add(String.valueOf(params[0].charAt(0)));
            splitParams.add(String.valueOf(params[0].charAt(1)));
            try{
                ChessPosition position = new ChessPosition(Integer.parseInt(splitParams.get(1)), findCorrespondingLetter(splitParams.get(0), teamColor));
                ChessPiece piece = postHandler.activeGame.game().getBoard().getPiece(position);
                if(piece != null){
                    Collection<ChessPosition> possibilities = calculatePossibilities(piece, position);
                    possibilities.add(position);
                    ChessBoardRender.render(postHandler.activeGame.game().getBoard(), possibilities, teamColor);
                } else {
                    return "Please enter a valid position";
                }
            } catch (Exception e){
                return "Please enter a valid position";
            }
        } else {
            return "Please enter a valid position";
        }
        return "";
    }

    private String getTeamColor() {
        boolean isBlack = postHandler.activeGame.blackUsername() != null &&
                postHandler.activeGame.blackUsername().equals(preHandler.currentUser.username());
        String teamColor;
        if(isBlack) {
            teamColor = "black";
        } else {
            teamColor = "white";
        }
        return teamColor;
    }

    private int findCorrespondingLetter(String letter, String color){
        if(color == "black"){
            Map<String, Integer> letterVals = Map.of(
                    "h", 1,
                    "g", 2,
                    "f", 3,
                    "e", 4,
                    "d", 5,
                    "c", 6,
                    "b", 7,
                    "a", 8
            );
            try{
                return letterVals.get(letter);
            } catch (Exception ex) {
                System.out.println(SET_TEXT_COLOR_PINK + "Please enter correct position format (ex: e2 e3");
            }
        } else {
            Map<String, Integer> letterVals = Map.of(
                    "h", 8,
                    "g", 7,
                    "f", 6,
                    "e", 5,
                    "d", 4,
                    "c", 3,
                    "b", 2,
                    "a", 1
            );
            try{
                return letterVals.get(letter);
            } catch (Exception ex) {
                System.out.println(SET_TEXT_COLOR_PINK + "Please enter correct position format (ex: e2 e3");
            }
        }
        return 0;
    }

    public String help() {
        return """
                You are currently playing/observing a game, please do one of the following:
                    "redraw" - view the board
                    "leave" - leave game or stop observing
                    "make-move" <column><row> <column><row> - to make move of a piece at col, row to position col, row
                    "resign" - to forfeit the game
                    "highlight" <piece square> - to highlight legal moves for a certain piece
                """;
    }

    public Collection<ChessPosition> calculatePossibilities(ChessPiece piece, ChessPosition position) {
        PieceMovesCalculator calculator = new PieceMovesCalculator(postHandler.activeGame.game().getBoard(), position);
        Collection<ChessMove> possibilities;
        if(piece.getPieceType() == ChessPiece.PieceType.KING){
            possibilities = calculator.kingMovesCalculator();
        } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN){
            possibilities = calculator.queenMovesCalculator();
        } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP){
            possibilities = calculator.bishopMovesCalculator();
        } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT){
            possibilities = calculator.knightMovesCalculator();
        } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK){
            possibilities = calculator.rookMovesCalculator();
        } else {
            possibilities = calculator.pawnMovesCalculator();
        }

        Collection<ChessPosition> positions = new ArrayList<>();
        for(ChessMove possibility : possibilities){
            positions.add(possibility.getEndPosition());
        }
        return positions;
    }
}
