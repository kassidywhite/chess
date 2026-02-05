package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMovesCalculator {
    Collection<ChessMove> possibilities;
    int curr_row;
    int curr_col;
    ChessPosition myPosition;
    ChessGame.TeamColor teamColor;
    ChessBoard board;
    ChessPiece.PieceType type;

    public PieceMovesCalculator(ChessBoard board, ChessPosition position) {
        possibilities = new ArrayList<>();
        myPosition = position;
        curr_row = myPosition.getRow();
        curr_col = myPosition.getColumn();
        teamColor = board.getPiece(position).getTeamColor();
        this.board = board;
        type = board.getPiece(position).getPieceType();
    }

    public Collection<ChessMove> PawnMovesCalculator(){
        // check white
        if(teamColor == ChessGame.TeamColor.WHITE){
            int[] close_piece = get_closest_piece_straight("up");
            if(curr_row + 1 < 8){
                if(close_piece[0] != curr_row + 1){
                    add_this(new ChessPosition(curr_row + 1, curr_col), null);
                    if(close_piece[0] != curr_row + 2 && curr_row == 2){
                        add_this(new ChessPosition(curr_row + 2, curr_col), null);
                    }
                }
                if(curr_col - 1 >= 1){
                    pawn_attack(new ChessPosition(curr_row + 1, curr_col - 1), null);
                }
                if(curr_col + 1 <= 8){
                    pawn_attack(new ChessPosition(curr_row + 1, curr_col + 1), null);
                }
            } else {
                ChessPiece.PieceType[] pieces = {
                        ChessPiece.PieceType.ROOK,
                        ChessPiece.PieceType.KNIGHT,
                        ChessPiece.PieceType.QUEEN,
                        ChessPiece.PieceType.BISHOP
                };
                for(int i = 0; i < 4; i++){
                    if(close_piece[0] != curr_row + 1){
                        add_this(new ChessPosition(curr_row + 1, curr_col), pieces[i]);
                    }
                    if(curr_col - 1 >= 1){
                        pawn_attack(new ChessPosition(curr_row + 1, curr_col - 1), pieces[i]);
                    }
                    if(curr_col + 1 <= 8){
                        pawn_attack(new ChessPosition(curr_row + 1, curr_col + 1), pieces[i]);
                    }
                }
            }
        }
        // check black
        if(teamColor == ChessGame.TeamColor.BLACK){
            int[] close_piece = get_closest_piece_straight("down");
            if(curr_row - 1 > 1){
                if(close_piece[0] != curr_row - 1){
                    add_this(new ChessPosition(curr_row - 1, curr_col), null);
                    if(close_piece[0] != curr_row - 2 && curr_row == 7){
                        add_this(new ChessPosition(curr_row - 2, curr_col), null);
                    }
                }
                if(curr_col - 1 >= 1){
                    pawn_attack(new ChessPosition(curr_row - 1, curr_col - 1), null);
                }
                if(curr_col + 1 <= 8){
                    pawn_attack(new ChessPosition(curr_row - 1, curr_col + 1), null);
                }
            } else {
                ChessPiece.PieceType[] pieces = {
                        ChessPiece.PieceType.ROOK,
                        ChessPiece.PieceType.KNIGHT,
                        ChessPiece.PieceType.QUEEN,
                        ChessPiece.PieceType.BISHOP
                };
                if(close_piece[0] != curr_row - 1){
                    for(int i = 0; i < 4; i++){
                        add_this(new ChessPosition(curr_row - 1, curr_col), pieces[i]);
                        if(curr_col - 1 >= 1){
                            pawn_attack(new ChessPosition(curr_row - 1, curr_col - 1), pieces[i]);
                        }
                        if(curr_col + 1 <= 8){
                            pawn_attack(new ChessPosition(curr_row - 1, curr_col + 1), pieces[i]);
                        }
                    }
                }
            }
        }

        return possibilities;
    }

    public Collection<ChessMove> KnightMovesCalculator(){
        // check below
        if (curr_row - 2 >= 1){
            if(curr_col - 1 >= 1){
                add_this(new ChessPosition(curr_row - 2, curr_col - 1), null);
            }
            if(curr_col + 1 <= 8){
                add_this(new ChessPosition(curr_row - 2, curr_col + 1), null);
            }
        }
        // check above
        if (curr_row + 2 <= 8){
            if(curr_col - 1 >= 1){
                add_this(new ChessPosition(curr_row + 2, curr_col - 1), null);
            }
            if(curr_col + 1 <= 8){
                add_this(new ChessPosition(curr_row + 2, curr_col + 1), null);
            }
        }
        // check left
        if(curr_col - 2 >= 1){
            if(curr_row - 1 >= 1){
                add_this(new ChessPosition(curr_row - 1, curr_col - 2), null);
            }
            if(curr_row + 1 <= 8){
                add_this(new ChessPosition(curr_row + 1, curr_col - 2), null);
            }
        }
        // check right
        if(curr_col + 2 <= 8){
            if(curr_row - 1 >= 1){
                add_this(new ChessPosition(curr_row - 1, curr_col + 2), null);
            }
            if(curr_row + 1 <= 8){
                add_this(new ChessPosition(curr_row + 1, curr_col + 2), null);
            }
        }

        return possibilities;
    }

    public Collection<ChessMove> QueenMovesCalculator(){
        RookMovesCalculator();
        BishopMovesCalculator();
        return possibilities;
    }

    public Collection<ChessMove> RookMovesCalculator(){
        // check down
        int[] close_piece = get_closest_piece_straight("down");
        if(close_piece[0] != 0){
            for(int r = curr_row - 1; r >= close_piece[0]; r -= 1){
                add_this(new ChessPosition(r, curr_col), null);
            }
        } else {
            int r = curr_row - 1;
            while(r >= 1){
                if(add_this_bool(new ChessPosition(r, curr_col), null)){
                    break;
                }
                r -= 1;
            }
        }
        // check up
        close_piece = get_closest_piece_straight("up");
        if(close_piece[0] != 0){
            for(int r = curr_row + 1; r <= close_piece[0]; r += 1){
                add_this(new ChessPosition(r, curr_col), null);
            }
        } else {
            int r = curr_row + 1;
            while(r <= 8){
                if(add_this_bool(new ChessPosition(r, curr_col), null)){
                    break;
                }
                r += 1;
            }
        }
        // check left
        close_piece = get_closest_piece_straight("left");
        if(close_piece[0] != 0){
            for(int c = curr_col - 1; c >= close_piece[1]; c -= 1){
                add_this(new ChessPosition(curr_row, c), null);
            }
        } else {
            int c = curr_col - 1;
            while (c >= 1) {
                if (add_this_bool(new ChessPosition(curr_row, c), null)) {
                    break;
                }
                c -= 1;
            }
        }
        // check right
        close_piece = get_closest_piece_straight("right");
        if(close_piece[0] != 0){
            for(int c = curr_col + 1; c <= close_piece[1]; c += 1){
                add_this(new ChessPosition(curr_row, c), null);
            }
        } else {
            int c = curr_col + 1;
            while (c <= 8) {
                if(add_this_bool(new ChessPosition(curr_row, c), null)) {
                    break;
                }
                c += 1;
            }
        }

        return possibilities;
    }

    public Collection<ChessMove> BishopMovesCalculator(){
        // checks lower left
        int r = curr_row - 1;
        int c = curr_col - 1;
        while(c <= 8 && c >= 1 && r <= 8 && r >= 1){
            if(add_this_bool(new ChessPosition(r, c), null)){
                break;
            }
            c -= 1;
            r -= 1;
        }
        //checks lower right
        r = curr_row - 1;
        c = curr_col + 1;
        while(c <= 8 && c >= 1 && r <= 8 && r >= 1){
            if(add_this_bool(new ChessPosition(r, c), null)){
                break;
            }
            c += 1;
            r -= 1;
        }
        //checks upper left
        r = curr_row + 1;
        c = curr_col - 1;
        while(c <= 8 && c >= 1 && r <= 8 && r >= 1){
            if(add_this_bool(new ChessPosition(r, c), null)){
                break;
            }
            c -= 1;
            r += 1;
        }
        //checks upper right
        r = curr_row + 1;
        c = curr_col + 1;
        while(c <= 8 && c >= 1 && r <= 8 && r >= 1){
            if(add_this_bool(new ChessPosition(r, c), null)){
                break;
            }
            c += 1;
            r += 1;
        }
        return possibilities;
    }

    public Collection<ChessMove> KingMovesCalculator(){
        if(curr_row - 1 >= 1){
            add_this(new ChessPosition(curr_row - 1, curr_col), null);
            if(curr_col - 1 >= 1){
                add_this(new ChessPosition(curr_row - 1, curr_col - 1), null);
            }
            if(curr_col + 1 <= 8){
                add_this(new ChessPosition(curr_row - 1, curr_col + 1), null);
            }
        }
        if(curr_col - 1 >= 1){
            add_this(new ChessPosition(curr_row, curr_col - 1), null);
        }
        if(curr_col + 1 <= 8){
            add_this(new ChessPosition(curr_row, curr_col + 1), null);
        }
        if(curr_row + 1 <= 8){
            add_this(new ChessPosition(curr_row + 1, curr_col), null);
            if(curr_col - 1 >= 1){
                add_this(new ChessPosition(curr_row + 1, curr_col - 1), null);
            }
            if(curr_col + 1 <= 8){
                add_this(new ChessPosition(curr_row + 1, curr_col + 1), null);
            }
        }

        return possibilities;
    }

    public void pawn_attack(ChessPosition position, ChessPiece.PieceType promotion){
        if(board.getPiece(position) != null) {
            if(board.getPiece(position).getTeamColor() != teamColor) {
                possibilities.add(new ChessMove(myPosition, new ChessPosition(position.getRow(), position.getColumn()), promotion));
            }
        }
    }

    public void add_this(ChessPosition position, ChessPiece.PieceType promotion){
        if(board.getPiece(position) != null){
            if(board.getPiece(position).getTeamColor() != teamColor){
                possibilities.add(new ChessMove(myPosition, new ChessPosition(position.getRow(), position.getColumn()), promotion));
            }
        } else {
            possibilities.add(new ChessMove(myPosition, new ChessPosition(position.getRow(), position.getColumn()), promotion));
        }
    }

    public boolean add_this_bool(ChessPosition position, ChessPiece.PieceType promotion){
        boolean is_piece = false;
        if(board.getPiece(position) != null){
            add_this(position, promotion);
            is_piece = true;
        } else {
            add_this(position, promotion);
        }
        return is_piece;
    }

    public int[] get_closest_piece_straight(String direction){
        int[] close_piece = new int[2];

        // check bottom
        int r = curr_row;
        int c = curr_col;
        if(direction.equals("down")){
            r -= 1;
            while(c <= 8 && c >= 1 && r <= 8 && r >= 1){
                if(board.getPiece(new ChessPosition(r, c)) != null){
                    close_piece[0] = r;
                    close_piece[1] = c;
                    break;
                }
                r -= 1;
            }
        } else if(direction.equals("up")){
            r += 1;
            while(c <= 8 && c >= 1 && r <= 8 && r >= 1){
                if(board.getPiece(new ChessPosition(r, c)) != null){
                    close_piece[0] = r;
                    close_piece[1] = c;
                    break;
                }
                r += 1;
            }
        } else if(direction.equals("left")){
            c -= 1;
            while(c <= 8 && c >= 1 && r <= 8 && r >= 1){
                if(board.getPiece(new ChessPosition(r, c)) != null){
                    close_piece[0] = r;
                    close_piece[1] = c;
                    break;
                }
                c -= 1;
            }
        } else if(direction.equals("right")){
            c += 1;
            while(c <= 8 && c >= 1 && r <= 8 && r >= 1){
                if(board.getPiece(new ChessPosition(r, c)) != null){
                    close_piece[0] = r;
                    close_piece[1] = c;
                    break;
                }
                c += 1;
            }
        }
        return close_piece;
    }
}