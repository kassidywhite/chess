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
    Collection<ChessPosition> pieces_putting_in_check;
    ChessBoard board_copy;

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        board = new ChessBoard();
        pieces_putting_in_check = new ArrayList<>();
        board_copy = board.clone();
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
        Collection<ChessMove> possibilities = ChessPiece.pieceMoves(board_copy, startPosition);
        for(ChessMove poss: possibilities){
            // something
            try {
                if(isInCheck(teamTurn)){

                }
            } catch (InvalidMoveException e) {
                throw new RuntimeException(e);
            }

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
        if(board.getPiece(new ChessPosition(move.getStartPosition().getRow(), move.getStartPosition().getColumn())).getTeamColor() != teamTurn){
            throw new InvalidMoveException();
        } else {
            if(validMoves(move.getStartPosition()).contains(move)){
                // updates board
                board.addPiece(move.getStartPosition(), new ChessPiece(null, null));
                ChessPosition this_pos = new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getColumn());
                ChessPiece add_this = new ChessPiece(board.getPiece(this_pos).getTeamColor(), board.getPiece(this_pos).getPieceType());
                board.addPiece(this_pos, add_this);
            }
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
                    if(target != null && target.getTeamColor() == teamColor && target.getPieceType() == ChessPiece.PieceType.KING){
                        pieces_putting_in_check.add(new ChessPosition(move.getStartPosition().getRow(), move.getStartPosition().getColumn()));
                        inCheck = true;
                    }
                }
                opponentMoves.clear();
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
        if(!isInCheck(teamColor)){
            return false;
        }
        Collection<ChessPosition> my_positions = getPositions(teamColor);
        for(ChessPosition position: my_positions){

        }
    }

    /**
     *
     * @param teamColor
     * @return the position of teamColor's king position
     */
    public ChessPosition getKingPosition(TeamColor teamColor){
        int row = 0;
        int col = 0;
        for(int r = 1; r <= 8; r++){
            for(int c = 1; c<=8; c++){
                ChessPiece this_piece = board.getPiece(new ChessPosition(r, c));
                if(this_piece != null && this_piece.getTeamColor().equals(teamColor) && this_piece.getPieceType().equals(ChessPiece.PieceType.KING)){
                    row = r;
                    col = c;
                    break;
                }
            }
        }
        return new ChessPosition(row, col);
    }

    public Collection<ChessPosition> getPositions(TeamColor teamColor){
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
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board) && teamColor == chessGame.teamColor && Objects.equals(pieces_putting_in_check, chessGame.pieces_putting_in_check);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board, teamColor, pieces_putting_in_check);
    }

    @Override
    public String toString() {
        return ", board=" + board +
                ", teamColor=" + teamColor;
    }
}
