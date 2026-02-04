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
    TeamColor teamColor;
    ChessPiece checkPiece;

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        board = new ChessBoard();
        Collection<ChessPiece> pieces = new ArrayList<>();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessBoard cloned_board = board.clone();
        Collection<ChessMove> possibilities = ChessPiece.pieceMoves(cloned_board, startPosition);
        for(ChessMove poss: possibilities){
            // something
        }
        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        boolean check = isInCheck(teamTurn);
        if(board.getPiece(new ChessPosition(move.getStartPosition().getRow(), move.getStartPosition().getColumn())).getTeamColor() != teamTurn){
            throw new InvalidMoveException();
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessBoard board_copy = board.clone();
        Collection<ChessMove> opponentMoves = new ArrayList<>();
        boolean inCheck = false;
        for(int r = 1; r <= 8; r++){
            for(int c = 1; c <= 8; c++){
                // check if there is a piece and if that piece is the correct color
                if(board_copy.getPiece(new ChessPosition(r, c)) != null){
                    if(board_copy.getPiece(new ChessPosition(r, c)).getTeamColor() != teamColor) {
                        opponentMoves = ChessPiece.pieceMoves(board_copy, new ChessPosition(r, c));
                    }
                }
                for(ChessMove move: opponentMoves){
                    ChessPiece target = board_copy.getPiece(move.getEndPosition());
                    if(target != null && target.getPieceType() == ChessPiece.PieceType.KING){
                        checkPiece = target;
                        inCheck = true;
                    }
                }
            }
        }
        return inCheck;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessBoard board_copy = board.clone();
        TeamColor opponentColor;
        if(teamColor.equals(TeamColor.WHITE)){
            opponentColor = TeamColor.BLACK;
        } else {
            opponentColor = TeamColor.WHITE;
        }
        boolean checkMate = false;
        if(isInCheck(teamColor)){
            // check if we can kill check piece
            Collection<ChessPosition> opponentPositions = getOpponentPositions(teamColor);
            for(ChessPosition position: opponentPositions){
                Collection<ChessMove> moves = ChessPiece.pieceMoves(board_copy, position);
                for(ChessMove move: moves){

                }
            }
        }
        return false;
    }

    public Collection<ChessPosition> getOpponentPositions(TeamColor teamColor){
        ChessBoard board_copy = board.clone();
        Collection<ChessPosition> opponentPositions = new ArrayList<>();
        for(int r = 1; r <= 8; r++){
            for(int c = 1; c <= 8; c++){
                if(board_copy.getPiece(new ChessPosition(r, c)) != null){
                    if(board_copy.getPiece(new ChessPosition(r, c)).getTeamColor() != teamColor) {
                        opponentPositions.add(new ChessPosition(r, c));
                    }
                }
            }
        }
        return opponentPositions;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(whiteMoves, chessGame.whiteMoves) && Objects.equals(blackMoves, chessGame.blackMoves) && teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board) && teamColor == chessGame.teamColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(whiteMoves, blackMoves, teamTurn, board, teamColor);
    }

    @Override
    public String toString() {
        return ", board=" + board +
                ", teamColor=" + teamColor;
    }
}
