package game.model;

import org.jetbrains.annotations.NotNull;

import static game.model.Position.pos;

public class Move {
    public Position dest;
    /**
     * Will not be changed after
     */
    @NotNull
    public Piece actor;

    public Move(int destx, int desty, @NotNull Piece actor) {
        dest = pos(destx, desty);
        this.actor = actor;
    }
}
