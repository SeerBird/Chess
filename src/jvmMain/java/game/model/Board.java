package game.model;

import org.apache.commons.math3.geometry.partitioning.utilities.OrderedTuple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static game.model.PieceType.king;
import static game.model.PieceType.pawn;

public class Board {
    /**
     * If you are making a turn, the check is yours.
     * Only moves that make you end the turn with no check are valid
     * While your move is being used to create a new board state, the check is yours
     * If the board state is valid, the opponent's check is calculated
     */
    boolean check;
    Map<Position, Piece> board;
    boolean whiteKingMoved;
    boolean blackKingMoved;
    Move lastMove;

    public Board() {
        check = false;
        whiteKingMoved = false;
        blackKingMoved = false;
        board = new HashMap<>();
        lastMove = new Move(4, 0, new Piece(true, king, 4, 0));
    }

    @Nullable
    public Board makeMove(@NotNull Move move) {
        Board res = new Board();
        res.board = new HashMap<>(board);
        res.board.get(move.targetx).set(move.targety, new Piece(move.actor, move.targetx, move.targety));
        res.board.get(move.actor.x).set(move.actor.y, null);
        //return null if ends in actor's check
        //if not, calculate check for the other player.
        return null;
    }

    /**
     * No need to check for king when capturing - you cannot start your turn attacking the king, the game should have ended
     *
     * @param actor a {@link Piece} on this board that we want to move
     * @return a list of {@link Board} states that the {@code actor} can achieve with its moves
     */
    @NotNull
    private ArrayList<Board> getPossibleMoves(@NotNull Piece actor) {
        ArrayList<Move> moves = new ArrayList<>();
        //region get moves that may result in check
        Piece target;
        switch (actor.type) {
            case pawn -> {
                int direction = pawnDirection(actor.color);
                //region en passant
                if ((target = lastMove.actor).type == pawn) {
                    if (Math.abs(target.y - lastMove.targety) == 2) {
                        if (target.y == actor.y) {
                            if (Math.abs(target.x - actor.y) == 1) {
                                moves.add(new Move(target.x, actor.y + direction, actor));
                            }
                        }
                    }
                }
                //endregion
                //region double jump
                if ((actor.color && actor.y == 1) || ((!actor.color) && actor.y == 6)) {
                    if (getPiece(actor.x, actor.y + direction) == null && getPiece(actor.x, actor.y + 2 * direction) == null) {
                        moves.add(new Move(actor.x, actor.y + 2 * direction, actor));
                    }
                }
                //endregion
                //region capture
                if (isFoe(actor.color, actor.x + 1, actor.y + direction)) {
                    moves.add(new Move(actor.x + 1, actor.y + direction, actor));
                }
                if (isFoe(actor.color, actor.x - 1, actor.y + direction)) {
                    moves.add(new Move(actor.x - 1, actor.y + direction, actor));
                }
                //endregion
                //region normal move
                if (actor.y + direction > 0 && actor.y + direction < 8) {
                    if (getPiece(actor.x, actor.y + direction) == null) {
                        moves.add(new Move(actor.x, actor.y + direction, actor));
                    }
                }
                //endregion
            }
            case bishop -> {
                addMoveRay(moves, actor, 1, 1);
                addMoveRay(moves, actor, 1, -1);
                addMoveRay(moves, actor, -1, -1);
                addMoveRay(moves, actor, -1, 1);
            }
            case knight -> {
                addMoveIfSimplyValid(moves, actor, actor.x + 1, actor.y + 2);
                addMoveIfSimplyValid(moves, actor, actor.x + 1, actor.y - 2);
                addMoveIfSimplyValid(moves, actor, actor.x - 1, actor.y - 2);
                addMoveIfSimplyValid(moves, actor, actor.x - 1, actor.y + 2);
                addMoveIfSimplyValid(moves, actor, actor.x + 2, actor.y + 1);
                addMoveIfSimplyValid(moves, actor, actor.x + 2, actor.y - 1);
                addMoveIfSimplyValid(moves, actor, actor.x - 2, actor.y - 1);
                addMoveIfSimplyValid(moves, actor, actor.x - 2, actor.y + 1);
            }
            case queen -> {
                addMoveRay(moves, actor, 1, 1);
                addMoveRay(moves, actor, 1, -1);
                addMoveRay(moves, actor, -1, -1);
                addMoveRay(moves, actor, -1, 1);
                addMoveRay(moves, actor, 0, 1);
                addMoveRay(moves, actor, 0, -1);
                addMoveRay(moves, actor, -1, 0);
                addMoveRay(moves, actor, 1, 0);
            }
            case rook -> {
                addMoveRay(moves, actor, 0, 1);
                addMoveRay(moves, actor, 0, -1);
                addMoveRay(moves, actor, -1, 0);
                addMoveRay(moves, actor, 1, 0);
            }
            case king -> {
                //region movement
                addMoveIfSimplyValid(moves, actor, actor.x + 1, actor.y + 1);
                addMoveIfSimplyValid(moves, actor, actor.x + 1, actor.y - 1);
                addMoveIfSimplyValid(moves, actor, actor.x - 1, actor.y - 1);
                addMoveIfSimplyValid(moves, actor, actor.x - 1, actor.y + 1);
                addMoveIfSimplyValid(moves, actor, actor.x + 1, actor.y);
                addMoveIfSimplyValid(moves, actor, actor.x, actor.y - 1);
                addMoveIfSimplyValid(moves, actor, actor.x - 1, actor.y);
                addMoveIfSimplyValid(moves, actor, actor.x, actor.y + 1);
                //endregion
                //region castle
                //endregion
            }
        }
        //endregion
        ArrayList<Board> futures = new ArrayList<>();
        //region enact each move to create potential board states
        //endregion
        //region validate new board states
        //endregion
        return futures;
    }

    //region move helper methods
    private void addMoveRay(ArrayList<Move> moves, Piece actor, int dx, int dy) {
        int targetx;
        int targety;
        Piece target;
        for (int i = 1; i < 7; i++) {
            targetx = actor.x + dx * i;
            targety = actor.y + dy * i;
            if ((target = getPiece(targetx, targety)) == null) {
                if (targetx > 7 || targetx < 0 || targety > 7 || targety < 0) {
                    break;
                }
                moves.add(new Move(targetx, targety, actor));
            } else if (target.color ^ actor.color) {
                moves.add(new Move(targetx, targety, actor));
                break;
            } else {
                break;
            }
        }
    }

    /**
     * A simply valid move is a move which makes the piece stay on the board and doesn't capture pieces of the same color
     */
    private void addMoveIfSimplyValid(ArrayList<Move> moves, Piece actor, int x, int y) {
        if (x > 7 || x < 0 || y > 7 || y < 0) {
            return;
        }
        Piece target;
        if ((target = getPiece(x, y)) != null) {
            if (target.color == actor.color) {
                return;
            }
        }
        moves.add(new Move(x, y, actor));
    }

    private int pawnDirection(boolean color) {
        if (color) {
            return 1;
        }
        return -1;
    }

    private boolean isFoe(boolean color, int x, int y) {
        Piece target = getPiece(x, y);
        return target != null && target.color ^ color;
    }
    //endregion

    public ArrayList<Board> getPossibleMoves(int x, int y) {
        Piece actor;
        if ((actor = getPiece(x, y)) == null) {
            return new ArrayList<>();
        }
        return getPossibleMoves(actor);
    }

    /**
     * Careful of recursion!
     */
    private boolean isAttacked(boolean defender, int x, int y) {
        for(ArrayList<Piece> column:board){
            for(Piece piece:column){

            }
        }
        return false;
    }

    @Nullable
    public Piece getPiece(int x, int y) {
        return board.get(x).get(y);
    }
}
