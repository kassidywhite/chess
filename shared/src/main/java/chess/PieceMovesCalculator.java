package chess;

import java.util.Collection;
import java.util.ArrayList;

public class PieceMovesCalculator {

    public PieceMovesCalculator(){

    }

    /**
     * Adds valid chess positions to the array called possibilities
     * Checks to see if they are at a valid row/column
     * Calls add_this to see if there is another piece in row/column
     * @return Collection <ChessMove> possibilities
     */
    public Collection<ChessMove> KingMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor, Collection<ChessMove> possibilities){
        int curr_row = myPosition.getRow();
        int curr_col = myPosition.getColumn();
        boolean col_minus_1_worked = true;
        boolean col_plus_1_worked = true;
        // Add possibilities below piece
        if(curr_row - 1 >= 0){
            if(curr_col - 1 >= 0){
                add_this(board, myPosition, pieceColor, curr_row - 1, curr_col - 1, possibilities);
            } else{
                col_minus_1_worked = false;
            }
            add_this(board, myPosition, pieceColor, curr_row - 1, curr_col, possibilities);
            if(curr_col + 1 <= 7){
                add_this(board, myPosition, pieceColor, curr_row - 1, curr_col + 1, possibilities);
            } else {
                col_plus_1_worked = false;
            }
        }
        // Add possibilities next to piece
        if(col_minus_1_worked){
            add_this(board, myPosition, pieceColor, curr_row, curr_col - 1, possibilities);
        }
        if(col_plus_1_worked){
            add_this(board, myPosition, pieceColor, curr_row, curr_col + 1, possibilities);
        }
        // Add possibilities above piece
        if(curr_row + 1 <= 7){
            if(col_minus_1_worked){
                add_this(board, myPosition, pieceColor, curr_row + 1, curr_col - 1, possibilities);
            }
            add_this(board, myPosition, pieceColor, curr_row + 1, curr_col, possibilities);
            if(col_plus_1_worked){
                add_this(board, myPosition, pieceColor, curr_row + 1, curr_col + 1, possibilities);
            }
        }
        return possibilities;
    }

    public Collection<ChessMove> BishopMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor, Collection<ChessMove> possibilities){
        int curr_row = myPosition.getRow();
        int curr_col = myPosition.getColumn();


        return possibilities;
    }

    /**
     * Checks to see if there is a piece at new_row, new_col
     * If yes, checks to see if piece is opposing team color
     * If all cases passed, adds piece to array of ChessMoves called possibilities
     */
    public void add_this(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor, int new_row, int new_col, Collection<ChessMove> possibilities){
        ChessPosition testing = new ChessPosition(new_row, new_col);
        if(board.getPiece(testing) == null){
            possibilities.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()), new ChessPosition(new_row, new_col), null));
        } else {
            if(board.getPiece(testing).getTeamColor() != pieceColor){
                possibilities.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()), new ChessPosition(new_row, new_col), null));
            }
        }
    }
}
