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
        board.resetBoard();
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
        if(board.getPiece(startPosition) == null){
            return moves;
        }
        Collection<ChessMove> possibilities = ChessPiece.pieceMoves(board, startPosition);
        for(ChessMove poss: possibilities){
            board_copy = board.clone();
            movePiece(poss);
            if(!isInCheck(board.getPiece(startPosition).getTeamColor())){
                moves.add(new ChessMove(poss.getStartPosition(), poss.getEndPosition(), poss.getPromotionPiece()));
            }

        }
        return moves;
    }

    /**
     * Adds a piece to board_copy at indicated move
     * @param move ChessMove
     */
    public void movePiece(ChessMove move) {
        board_copy.addPiece(move.getStartPosition(), null);
        ChessPosition curr_pos = move.getStartPosition();
        ChessPosition this_pos = new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getColumn());
        ChessPiece add_this = new ChessPiece(board.getPiece(curr_pos).getTeamColor(), board.getPiece(curr_pos).getPieceType());
        board_copy.addPiece(this_pos, add_this);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if(piece == null){
            throw new InvalidMoveException();
        }
        if(piece.getTeamColor() != teamTurn){
            throw new InvalidMoveException();
        }
        Collection<ChessMove> valid_moves = validMoves(move.getStartPosition());
        if(!valid_moves.contains(move)){
            throw new InvalidMoveException();
        }
        if(!isInCheck(teamTurn)){
            ChessPosition curr_pos = move.getStartPosition();
            ChessPosition this_pos = new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getColumn());
            ChessPiece add_this = new ChessPiece(board.getPiece(curr_pos).getTeamColor(), board.getPiece(curr_pos).getPieceType());
            if(move.getPromotionPiece() != null){
                add_this = new ChessPiece(board.getPiece(curr_pos).getTeamColor(), move.getPromotionPiece());
            }
            board.addPiece(move.getStartPosition(), null);
            board.addPiece(this_pos, add_this);
        } else {
            throw new InvalidMoveException();
        }
        if(teamTurn == TeamColor.BLACK){
            teamTurn = TeamColor.WHITE;
        } else {
            teamTurn = TeamColor.BLACK;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessMove> opponentMoves = new ArrayList<>();
        for(int r = 1; r <= 8; r++){
            for(int c = 1; c <= 8; c++){
                // check if there is a piece and if that piece is the correct color
                if(board_copy.getPiece(new ChessPosition(r, c)) != null && board_copy.getPiece(new ChessPosition(r, c)).getTeamColor() != teamColor){
                    opponentMoves = ChessPiece.pieceMoves(board_copy, new ChessPosition(r, c));
                }
                for(ChessMove move: opponentMoves){
                    ChessPiece target = board_copy.getPiece(move.getEndPosition());
                    if(target != null && target.getTeamColor() == teamColor && target.getPieceType() == ChessPiece.PieceType.KING){
                        return true;
                    }
                }
                opponentMoves.clear();
            }
        }
        return false;
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
            Collection<ChessMove> valid_moves = validMoves(position);
            if(valid_moves.size() > 0){
                return false;
            }
        }
        return true;
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
        Collection<ChessPosition> myPositions = new ArrayList<>();
        for(int r = 1; r <= 8; r++){
            for(int c = 1; c <= 8; c++){
                if(board_copy.getPiece(new ChessPosition(r, c)) != null){
                    if(board_copy.getPiece(new ChessPosition(r, c)).getTeamColor() == teamColor) {
                        myPositions.add(new ChessPosition(r, c));
                    }
                }
            }
        }
        return myPositions;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        boolean stalemate = false;
        if(isInCheck(teamColor)){
            return stalemate;
        }
        Collection<ChessPosition> myPositions = getPositions(teamColor);
        for(ChessPosition pos: myPositions){
            Collection<ChessMove> valid_moves = validMoves(pos);
            if(valid_moves.isEmpty()){
                stalemate = true;
            }
        }
        return stalemate;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
        board_copy = board.clone();
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
