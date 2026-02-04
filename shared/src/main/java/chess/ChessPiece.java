package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece implements Cloneable {
    ChessGame.TeamColor pieceColor;
    ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        PieceMovesCalculator calculator = new PieceMovesCalculator(board, myPosition);
        Collection<ChessMove> possibilities = new ArrayList<>();
        if(board.getPiece(myPosition).getPieceType().equals(PieceType.KING)){
            possibilities = calculator.KingMovesCalculator();
        }
        if(board.getPiece(myPosition).getPieceType().equals(PieceType.BISHOP)){
            possibilities = calculator.BishopMovesCalculator();
        }
        if(board.getPiece(myPosition).getPieceType().equals(PieceType.ROOK)){
            possibilities = calculator.RookMovesCalculator();
        }
        if(board.getPiece(myPosition).getPieceType().equals(PieceType.QUEEN)){
            possibilities = calculator.QueenMovesCalculator();
        }
        if(board.getPiece(myPosition).getPieceType().equals(PieceType.KNIGHT)){
            possibilities = calculator.KnightMovesCalculator();
        }
        if(board.getPiece(myPosition).getPieceType().equals(PieceType.PAWN)){
            possibilities = calculator.PawnMovesCalculator();
        }
        return possibilities;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public ChessPiece clone(){
        try{
            return (ChessPiece) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }
}
