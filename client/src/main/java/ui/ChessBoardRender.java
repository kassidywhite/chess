package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.*;

import java.util.Random;

import static ui.EscapeSequences.*;

public class ChessBoardRender {

    private static Random rand = new Random();

    public static void render(ChessBoard board, String playerView) {
        // board rendered from white player perspective
        String header = playerView.equals("white") ? "    a  b  c  d  e  f  g  h    " :
                "    h  g  f  e  d  c  b  a    ";
        printHeader(header);
        int r = playerView.equals("white") ? 8 : 1;
        while(r >= 1 && r <= 8) {
            System.out.print(SET_BG_COLOR_DARK_GREY);
            System.out.print(SET_TEXT_COLOR_PINK);
            System.out.print(" " + r + " ");
            int c = playerView.equals("white") ? 1 : 8;
            while(c >= 1 && c <= 8){
                ChessPosition position = new ChessPosition(r, c);
                String piece = getLetter(board.getPiece(position));
                String pieceColor = SET_TEXT_COLOR_RED;
                if (board.getPiece(position) != null){
                    pieceColor = (board.getPiece(position).getTeamColor() == ChessGame.TeamColor.WHITE ?
                            SET_TEXT_COLOR_RED : SET_TEXT_COLOR_BLUE);
                }
                if (r % 2 == 0){
                    if (c % 2 == 0){
                        System.out.print(SET_BG_COLOR_BLACK + pieceColor + piece);
                    } else {
                        System.out.print(SET_BG_COLOR_WHITE + pieceColor + piece);
                    }
                } else {
                    if (c % 2 == 0){
                        System.out.print(SET_BG_COLOR_WHITE + pieceColor + piece);
                    } else {
                        System.out.print(SET_BG_COLOR_BLACK + pieceColor + piece);
                    }
                }
                c = playerView.equals("white") ? c + 1 : c - 1;
            }
            System.out.print(SET_BG_COLOR_DARK_GREY);
            System.out.print(SET_TEXT_COLOR_PINK);
            System.out.print(" " + r + " ");
            System.out.println(RESET_BG_COLOR);
            r = playerView.equals("white") ? r - 1 : r + 1;
        }
        printHeader(header);
    }

    public static void printHeader(String header) {
        System.out.print(SET_BG_COLOR_DARK_GREY);
        System.out.print(SET_TEXT_COLOR_PINK);
        System.out.print(header);
        System.out.println(RESET_BG_COLOR);
    }

    private static String getLetter(ChessPiece piece) {
        if (piece == null){
            return "   ";
        } else if(piece.getPieceType().equals(ChessPiece.PieceType.KING)){
            return " K ";
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.BISHOP)){
            return " B ";
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.QUEEN)){
            return " Q ";
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.KNIGHT)){
            return " N ";
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.ROOK)){
            return " R ";
        } else {
            return " P ";
        }
    }
}
