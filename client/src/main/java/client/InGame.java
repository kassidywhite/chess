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
        if(postHandler.activeUserColor.equals("black")) {
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
        if(params.length == 2){
            List<String> splitParams = new ArrayList<>();
            splitParams.add(String.valueOf(params[0].charAt(0)));
            splitParams.add(String.valueOf(params[0].charAt(1)));
            splitParams.add(String.valueOf(params[1].charAt(0)));
            splitParams.add(String.valueOf(params[1].charAt(1)));
            int row1 = Integer.parseInt(splitParams.get(1));
            int row2 = Integer.parseInt(splitParams.get(3));
            int col1 = findCorrespondingLetter(splitParams.get(0));
            int col2 = findCorrespondingLetter(splitParams.get(2));
            if(row1 > 0 && row2 > 0 && row1 < 9 && row2 < 9 && col1 > 0 && col2 > 0 && col1 < 9 && col2 < 9){
                ChessPosition startPos = new ChessPosition(row1, col1);
                ChessPosition endPos = new ChessPosition(row2, col2);
                GameData activeGame = postHandler.activeGame;

                // check if it's a promotion piece
                ChessPiece thisPiece = postHandler.activeGame.game().getBoard().getPiece(startPos);
                String color = postHandler.activeUserColor;
                ChessPiece.PieceType promotion = null;

                if(endPos.getRow() == 8 && color.equals("white") && thisPiece.getPieceType() == ChessPiece.PieceType.PAWN){
                    while(promotion == null){
                        promotion = handlePromotion();
                    }
                }
                if(endPos.getRow() == 1 && color.equals("black") && thisPiece.getPieceType() == ChessPiece.PieceType.PAWN){
                    while(promotion == null){
                        promotion = handlePromotion();
                    }
                }

                ChessMove move = new ChessMove(startPos, endPos, promotion);
                try {
                    ws.makeMove(token, activeGame.gameID(), move);
                } catch (Exception e) {
                    preHandler.clientExceptionHandler(e);
                }
            } else {
                return "Please enter a valid start/end position";
            }
        } else {
            return "Please enter a valid start/end position";
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
        if(params.length == 1){
            List<String> splitParams = new ArrayList<>();
            splitParams.add(String.valueOf(params[0].charAt(0)));
            splitParams.add(String.valueOf(params[0].charAt(1)));
            try{
                ChessPosition position = new ChessPosition(Integer.parseInt(splitParams.get(1)), findCorrespondingLetter(splitParams.get(0)));
                ChessPiece piece = postHandler.activeGame.game().getBoard().getPiece(position);
                if(piece != null){
                    Collection<ChessPosition> possibilities = calculatePossibilities(piece, position);
                    possibilities.add(position);
                    ChessBoardRender.render(postHandler.activeGame.game().getBoard(), possibilities, postHandler.activeUserColor);
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

    public ChessPiece.PieceType handlePromotion() {
        System.out.println("Please specify what type of promotion piece you would like:");
        System.out.println("- queen, bishop, knight, rook");

        String line = "";
        if(preHandler.scanner.hasNext()){
            line = preHandler.scanner.nextLine();
        }
        try {
            String[] tokens = line.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            if(cmd.equals("queen")){
                return ChessPiece.PieceType.QUEEN;
            } else if(cmd.equals("rook")){
                return ChessPiece.PieceType.ROOK;
            } else if (cmd.equals("bishop")){
                return ChessPiece.PieceType.BISHOP;
            } else if (cmd.equals("knight")){
                return ChessPiece.PieceType.KNIGHT;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private int findCorrespondingLetter(String letter){
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
