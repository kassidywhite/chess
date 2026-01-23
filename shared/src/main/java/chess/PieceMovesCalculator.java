package chess;

import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

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
                add_this(board, myPosition, pieceColor, curr_row - 1, curr_col - 1, possibilities, null);
            } else{
                col_minus_1_worked = false;
            }
            add_this(board, myPosition, pieceColor, curr_row - 1, curr_col, possibilities, null);
            if(curr_col + 1 <= 7){
                add_this(board, myPosition, pieceColor, curr_row - 1, curr_col + 1, possibilities, null);
            } else {
                col_plus_1_worked = false;
            }
        }
        // Add possibilities next to piece
        if(col_minus_1_worked){
            add_this(board, myPosition, pieceColor, curr_row, curr_col - 1, possibilities, null);
        }
        if(col_plus_1_worked){
            add_this(board, myPosition, pieceColor, curr_row, curr_col + 1, possibilities, null);
        }
        // Add possibilities above piece
        if(curr_row + 1 <= 7){
            if(col_minus_1_worked){
                add_this(board, myPosition, pieceColor, curr_row + 1, curr_col - 1, possibilities, null);
            }
            add_this(board, myPosition, pieceColor, curr_row + 1, curr_col, possibilities, null);
            if(col_plus_1_worked){
                add_this(board, myPosition, pieceColor, curr_row + 1, curr_col + 1, possibilities, null);
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
            if(add_this_boolean(board, myPosition, pieceColor, curr_row, curr_col, possibilities, null)){
                break;
            }
        }
        // Checks the lower right side of the bishop
        curr_row = myPosition.getRow();
        curr_col = myPosition.getColumn();
        while((curr_row < 8) && (curr_row > 1) && (curr_col > 1) && (curr_col < 8)){
            curr_row -= 1;
            curr_col += 1;
            if(add_this_boolean(board, myPosition, pieceColor, curr_row, curr_col, possibilities, null)){
                break;
            }
        }
        // Checks the lower left side of the bishop
        curr_row = myPosition.getRow();
        curr_col = myPosition.getColumn();
        while((curr_row < 8) && (curr_row > 1) && (curr_col > 1) && (curr_col < 8)){
            curr_row -= 1;
            curr_col -= 1;
            if(add_this_boolean(board, myPosition, pieceColor, curr_row, curr_col, possibilities, null)){
                break;
            }
        }
        // Checks the upper right side of the bishop
        curr_row = myPosition.getRow();
        curr_col = myPosition.getColumn();
        while((curr_row < 8) && (curr_row > 1) && (curr_col > 1) && (curr_col < 8)){
            curr_row += 1;
            curr_col += 1;
            if(add_this_boolean(board, myPosition, pieceColor, curr_row, curr_col, possibilities, null)){
                break;
            }
        }


        return possibilities;
    }

    /**
     * checks below, then left, then right, then above current piece to find index of closest piece in referred directions
     * uses closest piece to determine
     * @param board
     * @param myPosition
     * @param pieceColor
     * @param possibilities
     * @return
     */
    public Collection<ChessMove> RookMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor, Collection<ChessMove> possibilities){
        int curr_row = myPosition.getRow();
        int curr_col = myPosition.getColumn();

        // Check below
        int[] close_piece = closest_piece_straight(board, curr_row, curr_col, "down");
        for(int i = close_piece[0]; i < curr_row; i++){
            add_this(board, myPosition, pieceColor, i, curr_col, possibilities, null);
        }

        //check left
        close_piece = closest_piece_straight(board, curr_row, curr_col, "left");
        for(int i = close_piece[1]; i < curr_col; i++){
            add_this(board, myPosition, pieceColor, curr_row, i, possibilities, null);
        }

        //check right
        close_piece = closest_piece_straight(board, curr_row, curr_col, "right");
        for(int i = close_piece[1]; i > curr_col; i--){
            add_this(board, myPosition, pieceColor, curr_row, i, possibilities, null);
        }

        // Check up
        close_piece = closest_piece_straight(board, curr_row, curr_col, "up");
        for(int i = close_piece[0]; i > curr_row; i--){
            add_this(board, myPosition, pieceColor, i, curr_col, possibilities, null);
        }
        return possibilities;
    }

    /**
     * checks below, left, above, right, lower left, lower right, upper left, upper right if queen can move there
     *
     * @param board current chess board
     * @param myPosition current piece position
     * @param pieceColor current team color
     * @param possibilities collection of chess move possibilities
     * @return possibilities
     */
    public Collection<ChessMove> QueenMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor, Collection<ChessMove> possibilities){
        int curr_row = myPosition.getRow();
        int curr_col = myPosition.getColumn();

        // Check below
        int[] close_piece_straight = closest_piece_straight(board, curr_row, curr_col, "down");
        for(int i = close_piece_straight[0]; i < curr_row; i++){
            add_this(board, myPosition, pieceColor, i, curr_col, possibilities, null);
        }

        //check left
        close_piece_straight = closest_piece_straight(board, curr_row, curr_col, "left");
        for(int i = close_piece_straight[1]; i < curr_col; i++){
            add_this(board, myPosition, pieceColor, curr_row, i, possibilities, null);
        }

        //check right
        close_piece_straight = closest_piece_straight(board, curr_row, curr_col, "right");
        for(int i = close_piece_straight[1]; i > curr_col; i--){
            add_this(board, myPosition, pieceColor, curr_row, i, possibilities, null);
        }

        // Check up
        close_piece_straight = closest_piece_straight(board, curr_row, curr_col, "up");
        for(int i = close_piece_straight[0]; i > curr_row; i--){
            add_this(board, myPosition, pieceColor, i, curr_col, possibilities, null);
        }

        // Checks the lower left side of the bishop
        curr_row = myPosition.getRow();
        curr_col = myPosition.getColumn();
        while((curr_row < 8) && (curr_row > 1) && (curr_col > 1) && (curr_col < 8)){
            curr_row -= 1;
            curr_col -= 1;
            if(add_this_boolean(board, myPosition, pieceColor, curr_row, curr_col, possibilities, null)){
                break;
            }
        }
        // Checks the lower right side of the bishop
        curr_row = myPosition.getRow();
        curr_col = myPosition.getColumn();
        while((curr_row < 8) && (curr_row > 1) && (curr_col < 8)){
            curr_row -= 1;
            curr_col += 1;
            if(add_this_boolean(board, myPosition, pieceColor, curr_row, curr_col, possibilities, null)){
                break;
            }
        }
        // Checks the upper left side of the bishop
        curr_row = myPosition.getRow();
        curr_col = myPosition.getColumn();
        while((curr_row < 8) && (curr_row > 1) && (curr_col > 1) && (curr_col < 8)){
            curr_row += 1;
            curr_col -= 1;
            if(add_this_boolean(board, myPosition, pieceColor, curr_row, curr_col, possibilities, null)){
                break;
            }
        }
        // Checks the upper right side of the bishop
        curr_row = myPosition.getRow();
        curr_col = myPosition.getColumn();
        while((curr_row < 8) && (curr_row > 1) && (curr_col < 8)){
            curr_row += 1;
            curr_col += 1;
            if(add_this_boolean(board, myPosition, pieceColor, curr_row, curr_col, possibilities, null)){
                break;
            }
        }
        return possibilities;
    }

    /**
     * checks the piece that is 1 to the bottom, above, left, and right and 1 below/right and 1 above/left of current piece
     * @param board current board
     * @param myPosition current position
     * @param pieceColor current team color
     * @param possibilities collection of possible moves
     * @return possibilities
     */
    public Collection<ChessMove> KnightMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor, Collection<ChessMove> possibilities){
        int curr_row = myPosition.getRow();;
        int curr_col = myPosition.getColumn();

        //checks bottom
        int r = curr_row - 2;
        for(int c = curr_col - 1; c <= curr_col + 1; c += 2){
            if(c <= 8 && c >= 1 && r <= 8 && r >= 1){
                add_this(board, myPosition, pieceColor, r, c, possibilities, null);
            }
        }
        // checks above
        r = curr_row + 2;
        for(int c = curr_col - 1; c <= curr_col + 1; c += 2){
            if(c <= 8 && c >= 1 && r <= 8 && r >= 1){
                add_this(board, myPosition, pieceColor, r, c, possibilities, null);
            }
        }
        //checks left
        int c = curr_col - 2;
        for(r = curr_row - 1; r <= curr_row + 1; r += 2){
            if(r <= 8 && r >= 1 && c <= 8 && c>=1){
                add_this(board, myPosition, pieceColor, r, c, possibilities, null);
            }
        }

        c = curr_col + 2;
        for(r = curr_row - 1; r <= curr_row + 1; r += 2){
            if(r <= 8 && r >= 1 && c <= 8 && c>=1){
                add_this(board, myPosition, pieceColor, r, c, possibilities, null);
            }
        }
        return possibilities;
    }

    public Collection<ChessMove> PawnMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor, Collection<ChessMove> possibilities){
        int curr_row = myPosition.getRow();
        int curr_col = myPosition.getColumn();
        List<ChessPiece.PieceType> pieceTypes = new ArrayList<>(Arrays.asList(ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK, ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.BISHOP));

        // for white pawns
        if(pieceColor.equals(ChessGame.TeamColor.WHITE)){
            //handling attacks
            if(curr_row + 1 < 8){
                if(curr_col + 1 < 8 && curr_col - 1 > 1){
                    pawn_attack(board, myPosition, pieceColor, curr_row + 1, curr_col + 1, possibilities, null);
                }
                pawn_attack(board, myPosition, pieceColor, curr_row + 1, curr_col - 1, possibilities, null);
            }

            // checking if can move forward
            int[] close_piece = closest_piece_straight(board, curr_row, curr_col, "up");
            if (close_piece[0] != curr_row + 1){
                add_this(board, myPosition, pieceColor, curr_row + 1, curr_col, possibilities, null);
            }

            // is it a promotion piece?
            if(curr_row + 1 == 8){
                for(int i = 0; i < 4; i++){
                    ChessPiece.PieceType promotionPiece = pieceTypes.get(i);
                    add_this(board, myPosition, pieceColor, curr_row + 1, curr_col, possibilities, promotionPiece);
                    pawn_attack(board, myPosition, pieceColor, curr_row + 1, curr_col + 1, possibilities, promotionPiece);
                    pawn_attack(board, myPosition, pieceColor, curr_row + 1, curr_col - 1, possibilities, promotionPiece);
                }

            }
            if(curr_row == 2){
                if(close_piece[0] != curr_row + 2 && close_piece[0] != curr_row + 1){
                    add_this(board, myPosition, pieceColor, curr_row + 2, curr_col, possibilities, null);
                }
            }
        }

        //for black pawns
        if(pieceColor.equals(ChessGame.TeamColor.BLACK)){
            //handling attacks
            if(curr_row - 1 > 1){
                if(curr_col - 1 > 1 && curr_col + 1 < 8){
                    pawn_attack(board, myPosition, pieceColor, curr_row - 1, curr_col + 1, possibilities, null);
                }
                pawn_attack(board, myPosition, pieceColor, curr_row - 1, curr_col - 1, possibilities, null);
            }

            // checking if can move forward
            int[] close_piece = closest_piece_straight(board, curr_row, curr_col, "down");
            if (close_piece[0] != curr_row - 1){
                add_this(board, myPosition, pieceColor, curr_row - 1, curr_col, possibilities, null);
            }

            // is it a promotion piece?
            if(curr_row - 1 == 1){
                for(int i = 0; i < 4; i++){
                    ChessPiece.PieceType promotionPiece = pieceTypes.get(i);
                    add_this(board, myPosition, pieceColor, 1, curr_col, possibilities, promotionPiece);
                    pawn_attack(board, myPosition, pieceColor, 1, curr_col + 1, possibilities, promotionPiece);
                    pawn_attack(board, myPosition, pieceColor, 1, curr_col - 1, possibilities, promotionPiece);
                }

            }
            if(curr_row == 7){
                if(close_piece[0] != curr_row - 2 && close_piece[0] != curr_row - 1){
                    add_this(board, myPosition, pieceColor, curr_row - 2, curr_col, possibilities, null);
                }
            }
        }
        return possibilities;
    }

    public void pawn_attack(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor, int new_row, int new_col, Collection<ChessMove> possibilities, ChessPiece.PieceType promotion){
        ChessPosition testing = new ChessPosition(new_row, new_col);
        boolean is_piece = false;
        if(board.getPiece(testing) != null){
            if(board.getPiece(testing).getTeamColor() != pieceColor){
                possibilities.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()), new ChessPosition(new_row, new_col), promotion));
            }
        }
    }



    /**
     * Checks to see if there is a piece at new_row, new_col
     * If yes, checks to see if piece is opposing team color
     * If all cases passed, adds piece to array of ChessMoves called possibilities
     * Only used for the KingMovesCalculator method
     * @param board current chess board
     * @param myPosition current position
     * @param pieceColor current team color
     * @param new_row what row you want to check
     * @param new_col what column you want to check
     * @param possibilities collection of possibilities
     */
    public void add_this(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor, int new_row, int new_col, Collection<ChessMove> possibilities, ChessPiece.PieceType promotion){
        ChessPosition testing = new ChessPosition(new_row, new_col);
        if(board.getPiece(testing) == null){
            possibilities.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()), testing, promotion));
        } else {
            if(board.getPiece(testing).getTeamColor() != pieceColor){
                possibilities.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()), new ChessPosition(new_row, new_col), promotion));
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
    public boolean add_this_boolean(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor, int new_row, int new_col, Collection<ChessMove> possibilities, ChessPiece.PieceType promotion){
        ChessPosition testing = new ChessPosition(new_row, new_col);
        boolean is_piece = false;
        if(board.getPiece(testing) == null){
            possibilities.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()), testing, promotion));
        } else {
            if(board.getPiece(testing).getTeamColor() != pieceColor){
                possibilities.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()), new ChessPosition(new_row, new_col), promotion));
            }
            is_piece = true;
        }
        return is_piece;
    }

    /**
     * returns the row and col values of the closest piece that is either down, up , left, or right of current piece
     * @param board current chess board
     * @param new_row current piece's row position
     * @param new_col current piece's column position
     * @param direction which direction you want to check (down, up, left, right)
     * @return int[] closest_piece, where first index is row position, second index is column position
     */
    public int[] closest_piece_straight(ChessBoard board, int new_row, int new_col, String direction){
        int[] closest_piece = new int[2];
        boolean is_piece = false;
        // check down
        if(direction.equals("down")){
            for(int i = new_row - 1; i >= 1; i--){
                ChessPosition testing = new ChessPosition(i, new_col);
                if(board.getPiece(testing) != null){
                    closest_piece[0] = i;
                    is_piece = true;
                }
            }
            if(!is_piece){
                closest_piece[0] = 1;
                return closest_piece;
            }
        }
        // check above
        if(direction.equals("up")){
            for(int i = new_row + 1; i <= 8; i++){
                ChessPosition testing = new ChessPosition(i, new_col);
                if(board.getPiece(testing) != null){
                    closest_piece[0] = i;
                    is_piece = true;
                }
            }
            if(!is_piece){
                closest_piece[0] = 8;
                return closest_piece;
            }
        }
        //check left
        if(direction.equals("left")){
            for(int i = new_col - 1; i >= 1; i--){
                ChessPosition testing = new ChessPosition(new_row, i);
                if(board.getPiece(testing) != null){
                    closest_piece[1] = i;
                    is_piece = true;
                }
            }
            if(!is_piece){
                closest_piece[1] = 1;
                return closest_piece;
            }
        }
        //check right
        if(direction.equals("right")){
            for(int i = new_col + 1; i <= 8; i++){
                ChessPosition testing = new ChessPosition(new_row, i);
                if(board.getPiece(testing) != null){
                    closest_piece[1] = i;
                    is_piece = true;
                }
            }
            if(!is_piece){
                closest_piece[1] = 8;
                return closest_piece;
            }
        }
        return closest_piece;
    }
}

