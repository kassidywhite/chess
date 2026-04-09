package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMovesCalculator {
    Collection<ChessMove> possibilities;
    int currRow;
    int currCol;
    ChessPosition myPosition;
    ChessGame.TeamColor teamColor;
    ChessBoard board;
    ChessPiece.PieceType type;

    public PieceMovesCalculator(ChessBoard board, ChessPosition position) {
        possibilities = new ArrayList<>();
        myPosition = position;
        currRow = myPosition.getRow();
        currCol = myPosition.getColumn();
        teamColor = board.getPiece(position).getTeamColor();
        this.board = board;
        type = board.getPiece(position).getPieceType();
    }

    public Collection<ChessMove> pawnMovesCalculator(){
        // check white
        if(teamColor == ChessGame.TeamColor.WHITE){
            int[] closePiece = getClosestStraightPiece("up");
            if(currRow + 1 < 8){
                if(closePiece[0] != currRow + 1){
                    addThis(new ChessPosition(currRow + 1, currCol), null);
                    if(closePiece[0] != currRow + 2 && currRow == 2){
                        addThis(new ChessPosition(currRow + 2, currCol), null);
                    }
                }
                if(currCol - 1 >= 1){
                    pawnAttack(new ChessPosition(currRow + 1, currCol - 1), null);
                }
                if(currCol + 1 <= 8){
                    pawnAttack(new ChessPosition(currRow + 1, currCol + 1), null);
                }
            } else {
                ChessPiece.PieceType[] pieces = {
                        ChessPiece.PieceType.ROOK,
                        ChessPiece.PieceType.KNIGHT,
                        ChessPiece.PieceType.QUEEN,
                        ChessPiece.PieceType.BISHOP
                };
                for(int i = 0; i < 4; i++){
                    if(closePiece[0] != currRow + 1){
                        addThis(new ChessPosition(currRow + 1, currCol), pieces[i]);
                    }
                    if(currCol - 1 >= 1){
                        pawnAttack(new ChessPosition(currRow + 1, currCol - 1), pieces[i]);
                    }
                    if(currCol + 1 <= 8){
                        pawnAttack(new ChessPosition(currRow + 1, currCol + 1), pieces[i]);
                    }
                }
            }
        }
        // check black
        if(teamColor != ChessGame.TeamColor.BLACK){
            return possibilities;
        }

        int[] closePiece = getClosestStraightPiece("down");
        if(currRow - 1 > 1){
            if(closePiece[0] != currRow - 1){
                addThis(new ChessPosition(currRow - 1, currCol), null);
                if(closePiece[0] != currRow - 2 && currRow == 7){
                    addThis(new ChessPosition(currRow - 2, currCol), null);
                }
            }
            if(currCol - 1 >= 1){
                pawnAttack(new ChessPosition(currRow - 1, currCol - 1), null);
            }
            if(currCol + 1 <= 8){
                pawnAttack(new ChessPosition(currRow - 1, currCol + 1), null);
            }
        } else {
            ChessPiece.PieceType[] pieces = {
                    ChessPiece.PieceType.ROOK,
                    ChessPiece.PieceType.KNIGHT,
                    ChessPiece.PieceType.QUEEN,
                    ChessPiece.PieceType.BISHOP
            };
            if(closePiece[0] != currRow - 1){
                for(int i = 0; i < 4; i++){
                    addThis(new ChessPosition(currRow - 1, currCol), pieces[i]);
                    if(currCol - 1 >= 1){
                        pawnAttack(new ChessPosition(currRow - 1, currCol - 1), pieces[i]);
                    }
                    if(currCol + 1 <= 8){
                        pawnAttack(new ChessPosition(currRow - 1, currCol + 1), pieces[i]);
                    }
                }
            }
        }

        return possibilities;
    }

    public Collection<ChessMove> knightMovesCalculator(){
        // check below
        if (currRow - 2 >= 1){
            if(currCol - 1 >= 1){
                addThis(new ChessPosition(currRow - 2, currCol - 1), null);
            }
            if(currCol + 1 <= 8){
                addThis(new ChessPosition(currRow - 2, currCol + 1), null);
            }
        }
        // check above
        if (currRow + 2 <= 8){
            if(currCol - 1 >= 1){
                addThis(new ChessPosition(currRow + 2, currCol - 1), null);
            }
            if(currCol + 1 <= 8){
                addThis(new ChessPosition(currRow + 2, currCol + 1), null);
            }
        }
        // check left
        if(currCol - 2 >= 1){
            if(currRow - 1 >= 1){
                addThis(new ChessPosition(currRow - 1, currCol - 2), null);
            }
            if(currRow + 1 <= 8){
                addThis(new ChessPosition(currRow + 1, currCol - 2), null);
            }
        }
        // check right
        if(currCol + 2 <= 8){
            if(currRow - 1 >= 1){
                addThis(new ChessPosition(currRow - 1, currCol + 2), null);
            }
            if(currRow + 1 <= 8){
                addThis(new ChessPosition(currRow + 1, currCol + 2), null);
            }
        }

        return possibilities;
    }

    public Collection<ChessMove> queenMovesCalculator(){
        rookMovesCalculator();
        bishopMovesCalculator();
        return possibilities;
    }

    public Collection<ChessMove> rookMovesCalculator(){
        // check down
        int[] closePiece = getClosestStraightPiece("down");
        if(closePiece[0] != 0){
            for(int r = currRow - 1; r >= closePiece[0]; r -= 1){
                addThis(new ChessPosition(r, currCol), null);
            }
        } else {
            int r = currRow - 1;
            while(r >= 1){
                if(addThisBool(new ChessPosition(r, currCol), null)){
                    break;
                }
                r -= 1;
            }
        }
        // check up
        closePiece = getClosestStraightPiece("up");
        if(closePiece[0] != 0){
            for(int r = currRow + 1; r <= closePiece[0]; r += 1){
                addThis(new ChessPosition(r, currCol), null);
            }
        } else {
            int r = currRow + 1;
            while(r <= 8){
                if(addThisBool(new ChessPosition(r, currCol), null)){
                    break;
                }
                r += 1;
            }
        }
        // check left
        closePiece = getClosestStraightPiece("left");
        if(closePiece[0] != 0){
            for(int c = currCol - 1; c >= closePiece[1]; c -= 1){
                addThis(new ChessPosition(currRow, c), null);
            }
        } else {
            int c = currCol - 1;
            while (c >= 1) {
                if (addThisBool(new ChessPosition(currRow, c), null)) {
                    break;
                }
                c -= 1;
            }
        }
        // check right
        closePiece = getClosestStraightPiece("right");
        if(closePiece[0] != 0){
            for(int c = currCol + 1; c <= closePiece[1]; c += 1){
                addThis(new ChessPosition(currRow, c), null);
            }
        } else {
            int c = currCol + 1;
            while (c <= 8) {
                if(addThisBool(new ChessPosition(currRow, c), null)) {
                    break;
                }
                c += 1;
            }
        }

        return possibilities;
    }

    public Collection<ChessMove> bishopMovesCalculator(){
        // checks lower left
        int r = currRow - 1;
        int c = currCol - 1;
        while(c <= 8 && c >= 1 && r <= 8 && r >= 1){
            if(addThisBool(new ChessPosition(r, c), null)){
                break;
            }
            c -= 1;
            r -= 1;
        }
        //checks lower right
        r = currRow - 1;
        c = currCol + 1;
        while(c <= 8 && c >= 1 && r <= 8 && r >= 1){
            if(addThisBool(new ChessPosition(r, c), null)){
                break;
            }
            c += 1;
            r -= 1;
        }
        //checks upper left
        r = currRow + 1;
        c = currCol - 1;
        while(c <= 8 && c >= 1 && r <= 8 && r >= 1){
            if(addThisBool(new ChessPosition(r, c), null)){
                break;
            }
            c -= 1;
            r += 1;
        }
        //checks upper right
        r = currRow + 1;
        c = currCol + 1;
        while(c <= 8 && c >= 1 && r <= 8 && r >= 1){
            if(addThisBool(new ChessPosition(r, c), null)){
                break;
            }
            c += 1;
            r += 1;
        }
        return possibilities;
    }

    public Collection<ChessMove> kingMovesCalculator(){
        if(currRow - 1 >= 1){
            addThis(new ChessPosition(currRow - 1, currCol), null);
            if(currCol - 1 >= 1){
                addThis(new ChessPosition(currRow - 1, currCol - 1), null);
            }
            if(currCol + 1 <= 8){
                addThis(new ChessPosition(currRow - 1, currCol + 1), null);
            }
        }
        if(currCol - 1 >= 1){
            addThis(new ChessPosition(currRow, currCol - 1), null);
        }
        if(currCol + 1 <= 8){
            addThis(new ChessPosition(currRow, currCol + 1), null);
        }
        if(currRow + 1 <= 8){
            addThis(new ChessPosition(currRow + 1, currCol), null);
            if(currCol - 1 >= 1){
                addThis(new ChessPosition(currRow + 1, currCol - 1), null);
            }
            if(currCol + 1 <= 8){
                addThis(new ChessPosition(currRow + 1, currCol + 1), null);
            }
        }

        return possibilities;
    }

    public void pawnAttack(ChessPosition position, ChessPiece.PieceType promotion){
        if(board.getPiece(position) != null) {
            if(board.getPiece(position).getTeamColor() != teamColor) {
                possibilities.add(new ChessMove(myPosition, new ChessPosition(position.getRow(), position.getColumn()), promotion));
            }
        }
    }

    public void addThis(ChessPosition position, ChessPiece.PieceType promotion){
        if(board.getPiece(position) != null){
            pawnAttack(position, promotion);
        } else {
            possibilities.add(new ChessMove(myPosition, new ChessPosition(position.getRow(), position.getColumn()), promotion));
        }
    }

    public boolean addThisBool(ChessPosition position, ChessPiece.PieceType promotion){
        boolean isPiece = false;
        if(board.getPiece(position) != null){
            addThis(position, promotion);
            isPiece = true;
        } else {
            addThis(position, promotion);
        }
        return isPiece;
    }

    public int[] getClosestStraightPiece(String direction) {
        int[] closest_piece = new int[2];

        //check down
        if (direction.equals("down")) {
            for (int r = currRow - 1; r >= 1; r--) {
                ChessPosition testing = new ChessPosition(r, currCol);
                if (board.getPiece(testing) != null) {
                    closest_piece[0] = r;
                    closest_piece[1] = currCol;
                    return closest_piece;
                }
            }
        }
        //check up
        if (direction.equals("up")) {
            for (int r = currRow + 1; r <= 8; r++) {
                ChessPosition testing = new ChessPosition(r, currCol);
                if (board.getPiece(testing) != null) {
                    closest_piece[0] = r;
                    closest_piece[1] = currCol;
                    return closest_piece;
                }
            }
        }
        //check left
        if (direction.equals("left")) {
            for (int c = currCol - 1; c >= 1; c--) {
                ChessPosition testing = new ChessPosition(currRow, c);
                if (board.getPiece(testing) != null) {
                    closest_piece[0] = currRow;
                    closest_piece[1] = c;
                    return closest_piece;
                }
            }
        }
        //check right
        if (direction.equals("right")) {
            for (int c = currCol + 1; c <= 8; c++) {
                ChessPosition testing = new ChessPosition(currRow, c);
                if (board.getPiece(testing) != null) {
                    closest_piece[0] = currRow;
                    closest_piece[1] = c;
                    return closest_piece;
                }
            }
        }
        return closest_piece;
    }
}