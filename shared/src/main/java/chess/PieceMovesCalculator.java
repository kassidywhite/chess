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
     * @param board current chess board
     * @param myPosition current piece position
     * @param pieceColor current piece color
     * @param possibilities array of possible chess moves, starts off as an empty array
     * @return possibilities
     */
    public Collection<ChessMove> KingMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor, Collection<ChessMove> possibilities){
        int curr_row = myPosition.getRow();
        int curr_col = myPosition.getColumn();
        boolean col_minus_1_worked = true;
        boolean col_plus_1_worked = true;
        // Add possibilities below piece
        if(curr_row - 1 >= 0){
            if(curr_col - 1 >= 0){
                king_add_this(board, myPosition, pieceColor, curr_row - 1, curr_col - 1, possibilities);
            } else{
                col_minus_1_worked = false;
            }
            king_add_this(board, myPosition, pieceColor, curr_row - 1, curr_col, possibilities);
            if(curr_col + 1 <= 7){
                king_add_this(board, myPosition, pieceColor, curr_row - 1, curr_col + 1, possibilities);
            } else {
                col_plus_1_worked = false;
            }
        }
        // Add possibilities next to piece
        if(col_minus_1_worked){
            king_add_this(board, myPosition, pieceColor, curr_row, curr_col - 1, possibilities);
        }
        if(col_plus_1_worked){
            king_add_this(board, myPosition, pieceColor, curr_row, curr_col + 1, possibilities);
        }
        // Add possibilities above piece
        if(curr_row + 1 <= 7){
            if(col_minus_1_worked){
                king_add_this(board, myPosition, pieceColor, curr_row + 1, curr_col - 1, possibilities);
            }
            king_add_this(board, myPosition, pieceColor, curr_row + 1, curr_col, possibilities);
            if(col_plus_1_worked){
                king_add_this(board, myPosition, pieceColor, curr_row + 1, curr_col + 1, possibilities);
            }
        }
        return possibilities;
    }

    /**
     * checks the upper left, then lower right, then lower left, then upper right quadrants of the bishop piece
     * adds valid positions to possibilities array
     *
     * @param board takes in the current chess board
     * @param myPosition takes in the current piece position
     * @param pieceColor the current piece's team color
     * @param possibilities the array of the piece's possibilities
     * @return possibilities
     */
    public Collection<ChessMove> BishopMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor, Collection<ChessMove> possibilities){
        // Checks the upper left side of the bishop
        int curr_row = myPosition.getRow();
        int curr_col = myPosition.getColumn();
        while((curr_row < 8) && (curr_row > 1) && (curr_col > 1) && (curr_col < 8)){
            curr_row += 1;
            curr_col -= 1;
            if(add_this(board, myPosition, pieceColor, curr_row, curr_col, possibilities)){
                break;
            }
        }
        // Checks the lower right side of the bishop
        curr_row = myPosition.getRow();
        curr_col = myPosition.getColumn();
        while((curr_row < 8) && (curr_row > 1) && (curr_col > 1) && (curr_col < 8)){
            curr_row -= 1;
            curr_col += 1;
            if(add_this(board, myPosition, pieceColor, curr_row, curr_col, possibilities)){
                break;
            }
        }
        // Checks the lower left side of the bishop
        curr_row = myPosition.getRow();
        curr_col = myPosition.getColumn();
        while((curr_row < 8) && (curr_row > 1) && (curr_col > 1) && (curr_col < 8)){
            curr_row -= 1;
            curr_col -= 1;
            if(add_this(board, myPosition, pieceColor, curr_row, curr_col, possibilities)){
                break;
            }
        }
        // Checks the upper right side of the bishop
        curr_row = myPosition.getRow();
        curr_col = myPosition.getColumn();
        while((curr_row < 8) && (curr_row > 1) && (curr_col > 1) && (curr_col < 8)){
            curr_row += 1;
            curr_col += 1;
            if(add_this(board, myPosition, pieceColor, curr_row, curr_col, possibilities)){
                break;
            }
        }


        return possibilities;
    }

    public Collection<ChessMove> RookMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor, Collection<ChessMove> possibilities){
        int curr_row = myPosition.getRow();
        int curr_col = myPosition.getColumn();
        while((curr_row > 1)){
            curr_row -= 1;
            if(add_this(board, myPosition, pieceColor, curr_row, curr_col, possibilities)){
                break;
            }
        }
        curr_row = myPosition.getRow();
        while((curr_col > 1) && (curr_col < 8)){
            curr_col -= 1;
            if(add_this(board, myPosition, pieceColor, curr_row, curr_col, possibilities)){
                break;
            }
        }
        curr_col = myPosition.getColumn();
        while((curr_col > 1) && (curr_col < 8)){
            curr_col += 1;
            if(add_this(board, myPosition, pieceColor, curr_row, curr_col, possibilities)){
                break;
            }
        }
        curr_col = myPosition.getColumn();
        while((curr_row > 1) && (curr_row < 8)){
            curr_row += 1;
            if(add_this(board, myPosition, pieceColor, curr_row, curr_col, possibilities)){
                break;
            }
        }
        return possibilities;
    }

    /**
     * Checks to see if there is a piece at new_row, new_col
     * If yes, checks to see if piece is opposing team color
     * If all cases passed, adds piece to array of ChessMoves called possibilities
     * Only used for the KingMovesCalculator method
     */
    public void king_add_this(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor, int new_row, int new_col, Collection<ChessMove> possibilities){
        ChessPosition testing = new ChessPosition(new_row, new_col);
        if(board.getPiece(testing) == null){
            possibilities.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()), testing, null));
        } else {
            if(board.getPiece(testing).getTeamColor() != pieceColor){
                possibilities.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()), new ChessPosition(new_row, new_col), null));
            }
        }
    }

    /**
     * Checks to see if there is a piece at new_row, new_col
     * If yes, checks to see if piece is opposing team color
     * Marks that it has captured a piece and returns a boolean reporting that a piece was captured
     *
     * @param board current chess board
     * @param myPosition current piece position
     * @param pieceColor current piece color
     * @param new_row row we want to check
     * @param new_col column we want to check
     * @param possibilities array of piece move possibilities
     * @return took_piece boolean
     */
    public boolean add_this(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor, int new_row, int new_col, Collection<ChessMove> possibilities){
        ChessPosition testing = new ChessPosition(new_row, new_col);
        boolean took_piece = false;
        if(board.getPiece(testing) == null){
            possibilities.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()), testing, null));
        } else {
            if(board.getPiece(testing).getTeamColor() != pieceColor){
                took_piece = true;
                possibilities.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()), new ChessPosition(new_row, new_col), null));
            }
        }
        return took_piece;
    }
    public Object[] move_straight(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor, int new_row, int new_col, Collection<ChessMove> possibilities, String direction){
        Object[] closest_piece = new Object[3]; // will return the closest piece in a certain direction's row and column values as well as its team color
        if(direction.equals("down")){
            for(int i = new_row; i >= 1; i--){
                ChessPosition testing = new ChessPosition(i, new_col);
                if(board.getPiece(testing) != null){
                    closest_piece[0] = i;
                    closest_piece[1] = new_col;
                    closest_piece[2] = board.getPiece(testing).getTeamColor();
                }
            }
        }
        if(direction.equals("up")){
            for(int i = new_row; i <= 8; i++){
                ChessPosition testing = new ChessPosition(i, new_col);
                if(board.getPiece(testing) != null){
                    closest_piece[0] = i;
                    closest_piece[1] = new_col;
                    closest_piece[2] = board.getPiece(testing).getTeamColor();
                }
            }
        }
        if(direction.equals("left")){
            for(int i = new_col; i >= 1; i--){
                ChessPosition testing = new ChessPosition(new_row, i);
                if(board.getPiece(testing) != null){
                    closest_piece[0] = new_row;
                    closest_piece[1] = i;
                    closest_piece[2] = board.getPiece(testing).getTeamColor();
                }
            }
        }
        if(direction.equals("right")){
            for(int i = new_col; i <= 8; i++){
                ChessPosition testing = new ChessPosition(new_row, i);
                if(board.getPiece(testing) != null){
                    closest_piece[0] = new_row;
                    closest_piece[1] = i;
                    closest_piece[2] = board.getPiece(testing).getTeamColor();
                }
            }
        }
        return closest_piece;
    }

//    public int[] find_closest_piece(ChessBoard board, ChessPosition myPosition, int curr_row, int curr_col){
//        int[] row_col = {};
//        return row_col;
//    }
}
