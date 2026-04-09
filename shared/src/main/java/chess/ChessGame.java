package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor teamTurn;
    ChessBoard board;
    ChessBoard boardCopy;
    public boolean gameOver = false;

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
        boardCopy = board.clone();
    }

    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    public enum TeamColor {
        WHITE,
        BLACK
    }

    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        if(board.getPiece(startPosition) == null){
            return moves;
        }
        Collection<ChessMove> possibilities = ChessPiece.pieceMoves(board, startPosition);
        for(ChessMove poss: possibilities){
            boardCopy = board.clone();
            movePiece(boardCopy, poss);
            if(!isInCheck(board.getPiece(startPosition).getTeamColor())){
                moves.add(new ChessMove(poss.getStartPosition(), poss.getEndPosition(), poss.getPromotionPiece()));
            }

        }
        return moves;
    }

    public void movePiece(ChessBoard boardToUse, ChessMove move) {
        ChessPiece currPiece = boardToUse.getPiece(move.getStartPosition());
        ChessPosition thisPos = new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getColumn());
        ChessPiece addThis = new ChessPiece(currPiece.getTeamColor(), currPiece.getPieceType());
        boardToUse.addPiece(move.getStartPosition(), null);
        boardToUse.addPiece(thisPos, addThis);
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if(piece == null){
            throw new InvalidMoveException();
        }
        if(piece.getTeamColor() != teamTurn){
            throw new InvalidMoveException();
        }
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if(!validMoves.contains(move)){
            throw new InvalidMoveException();
        }
        if(!isInCheck(teamTurn)){
            ChessPosition currPos = move.getStartPosition();
            ChessPosition thisPos = new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getColumn());
            ChessPiece addThis = new ChessPiece(board.getPiece(currPos).getTeamColor(), board.getPiece(currPos).getPieceType());
            if(move.getPromotionPiece() != null){
                addThis = new ChessPiece(board.getPiece(currPos).getTeamColor(), move.getPromotionPiece());
            }
            board.addPiece(move.getStartPosition(), null);
            board.addPiece(thisPos, addThis);

            boardCopy = board.clone();
        } else {
            throw new InvalidMoveException();
        }
        if(teamTurn == TeamColor.BLACK){
            teamTurn = TeamColor.WHITE;
        } else {
            teamTurn = TeamColor.BLACK;
        }
    }

    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = new ChessPosition(0, 0);

        for(int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPiece checkThis = boardCopy.getPiece(new ChessPosition(r, c));
                if(checkThis != null &&
                        checkThis.getPieceType().equals(ChessPiece.PieceType.KING)
                        && checkThis.getTeamColor() == teamColor){
                    kingPos = new ChessPosition(r, c);
                    break;
                }
            }
            if (kingPos.getRow() != 0){
                break;
            }
        }

        Collection<ChessMove> opponentMoves = new ArrayList<>();
        for(int r = 1; r <= 8; r++){ // change back to r = 1
            for(int c = 1; c <= 8; c++){
                
                ChessPiece piece = boardCopy.getPiece(new ChessPosition(r, c));
                Collection<ChessMove> moves = List.of();
                
                if (piece != null && piece.getTeamColor() != teamColor){
                    moves = ChessPiece.pieceMoves(boardCopy, new ChessPosition(r, c));
                }
                
                for (ChessMove move : moves) {
                    if (move.getEndPosition().equals(kingPos)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        if(!isInCheck(teamColor)){
            return false;
        }
        
        Collection<ChessPosition> myPositions = getPositions(teamColor);
        for(ChessPosition position: myPositions){
            ChessPiece piece = board.getPiece(position);
            if(piece != null){
                Collection<ChessMove> moves = ChessPiece.pieceMoves(board, position);
                
                for (ChessMove move : moves){
                    movePiece(boardCopy, move);
                    
                    if(!isInCheck(teamColor)) {
                        return false;
                    }
                    boardCopy = board.clone();
                }
            }
        }
        gameOver = true;
        return true;
    }

    public Collection<ChessPosition> getPositions(TeamColor teamColor){
        ChessBoard boardCopy = board.clone();
        Collection<ChessPosition> myPositions = new ArrayList<>();
        for(int r = 1; r <= 8; r++){
            for(int c = 1; c <= 8; c++){
                if(boardCopy.getPiece(new ChessPosition(r, c)) != null){
                    if(boardCopy.getPiece(new ChessPosition(r, c)).getTeamColor() == teamColor) {
                        myPositions.add(new ChessPosition(r, c));
                    }
                }
            }
        }
        return myPositions;
    }

    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor)){
            return false;
        }
        Collection<ChessPosition> myPositions = getPositions(teamColor);
        for(ChessPosition pos: myPositions){
            Collection<ChessMove> validMoves = validMoves(pos);
            if(!validMoves.isEmpty()){
                return false;
            }
        }
        gameOver = true;
        return true;
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
        boardCopy = board.clone();
    }

    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board) && Objects.equals(boardCopy, chessGame.boardCopy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board, boardCopy);
    }

    @Override
    public String toString() {
        return ", board=" + board;
    }
}
