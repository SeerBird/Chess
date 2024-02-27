package game.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import game.model.PieceType.*;

import java.util.ArrayList;

import static game.model.Color.black;
import static game.model.PieceType.king;
import static game.model.PieceType.pawn;

public class Board {
    ArrayList<ArrayList<Piece>> board;
    Move lastMove;

    public Board() {
        board = new ArrayList<>();
        lastMove = new Move(4, 0, new Piece(black, king, 4, 0));
    }

    @NotNull
    private ArrayList<Move> getPossibleMoves(@NotNull Piece actor) {
        ArrayList<Move> moves = new ArrayList<>();
        Piece target;
        switch (actor.type) {
            case pawn -> {
                int direction = pawnDirection(actor.color);
                //region en passant
                if ((target = lastMove.actor).type == pawn) {
                    if (Math.abs(target.y - lastMove.targety) == 2) {
                        if (target.y == actor.y) {
                            if (Math.abs(target.x - actor.y) == 1) {
                                moves.add(new Move(target.x, actor.y + pawnDirection(actor.color), actor));
                            }
                        }
                    }
                }
                //endregion
                //region double jump
                if ((actor.color == black && actor.y == 1) || (actor.color != black && actor.y == 6)) {
                    if (getPiece(actor.x, actor.y + direction) == null && getPiece(actor.x, actor.y + 2 * direction) == null) {
                        moves.add(new Move(actor.x, actor.y + 2 * direction, actor));
                    }
                }
                //endregion
                //region capture
                //endregion
            }
            case bishop -> {

            }
            case knight -> {

            }
            case queen -> {

            }
            case rook -> {

            }
            case king -> {

            }
        }
        //region check for check
        //endregion
        return moves;
    }

    private int pawnDirection(Color color) {
        if (color == black) {
            return 1;
        }
        return -1;
    }

    @Nullable
    private Boolean isFriend(Color color, int x, int y) {
        Piece target = getPiece(x, y);
        return target == null ? null : target.color == color;
    }

    public ArrayList<Move> getPossibleMoves(int x, int y) {
        Piece actor;
        if ((actor = getPiece(x, y)) == null) {
            return new ArrayList<>();
        }
        return getPossibleMoves(actor);
    }

    @Nullable
    public Piece getPiece(int x, int y) {
        return board.get(x).get(y);
    }
}
