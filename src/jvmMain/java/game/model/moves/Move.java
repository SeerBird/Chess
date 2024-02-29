package game.model.moves;

import game.model.Piece;
import game.model.Position;
import org.jetbrains.annotations.NotNull;

import static game.model.Position.pos;

public class Move {
    public Position dest;
    public MoveMode mode;
    /**
     * Will not be changed after
     */
    @NotNull
    public Piece actor;

    public Move(int destx, int desty, @NotNull Piece actor, MoveMode mode) {
        dest = pos(destx, desty);
        this.actor = actor;
        this.mode = mode;
    }
}
