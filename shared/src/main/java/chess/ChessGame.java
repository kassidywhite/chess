package chess;

import java.util.ArrayList;
import java.util.Collection;
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

    public Collection<ChessMove> validMoves(ChessMove startPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        if(board.getPiece(startPosition) == null){
            return moves;
        }
        Collection<ChessMove> possibilities = ChessPiece.pieceMoves(board, startPosition);
        for(ChessMove poss: possibilities){
            boardCopy = board.clone();
            movePiece(poss);
            if(!isInCheck(board.getPiece(startPosition).getTeamColor())){
                moves.add(new ChessMove(poss.getStartPosition(), poss.getEndPosition(), poss.getPromotionPiece()));
            }

        }
        return moves;
    }

    public void movePiece(ChessMove move) {
        ChessPiece currPiece = board.getPiece(move.getStartPosition());
        ChessMove thisPos = new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getColumn());
        ChessPiece addThis = new ChessPiece(currPiece.getTeamColor(), currPiece.getPieceType());
        boardCopy.addPiece(move.getStartPosition(), null);
        boardCopy.addPiece(thisPos, addThis);
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
            ChessMove currPos = move.getStartPosition();
            ChessMove thisPos = new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getColumn());
            ChessPiece addThis = new ChessPiece(board.getPiece(currPos).getTeamColor(), board.getPiece(currPos).getPieceType());
            if(move.getPromotionPiece() != null){
                addThis = new ChessPiece(board.getPiece(currPos).getTeamColor(), move.getPromotionPiece());
            }
            board.addPiece(move.getStartPosition(), null);
            board.addPiece(thisPos, addThis);
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
        Collection<ChessMove> opponentMoves = new ArrayList<>();
        for(int r = 1; r <= 8; r++){
            for(int c = 1; c <= 8; c++){
                // check if there is a piece and if that piece is the correct color
                if(boardCopy.getPiece(new ChessPosition(r, c)) != null && boardCopy.getPiece(new ChessPosition(r, c)).getTeamColor() != teamColor){
                    opponentMoves = ChessPiece.pieceMoves(boardCopy, new ChessPosition(r, c));
                }
                for(ChessMove move: opponentMoves){
                    ChessPiece target = boardCopy.getPiece(move.getEndPosition());
                    if(target != null && target.getTeamColor() == teamColor && target.getPieceType() == ChessPiece.PieceType.KING){
                        return true;
                    }
                }
                opponentMoves.clear();
            }
        }
        return false;
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        if(!isInCheck(teamColor)){
            return false;
        }
        Collection<ChessMove> myPositions = getPositions(teamColor);
        for(ChessMove position: myPositions){
            Collection<ChessMove> validMoves = validMoves(position);
            if(validMoves.size() > 0){
                return false;
            }
        }
        return true;
    }

    public Collection<ChessMove> getPositions(TeamColor teamColor){
        ChessBoard boardCopy = board.clone();
        Collection<ChessMove> myPositions = new ArrayList<>();
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
        Collection<ChessMove> myPositions = getPositions(teamColor);
        for(ChessMove pos: myPositions){
            Collection<ChessMove> validMoves = validMoves(pos);
            if(!validMoves.isEmpty()){
                return false;
            }
        }
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
