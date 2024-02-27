package game.model;

import org.jetbrains.annotations.NotNull;

public class Move {
    public int targetx;
    public int targety;
    /**
     * Will not be changed after
     */
    @NotNull
    public Piece actor;

    public Move(int x, int y, @NotNull Piece actor) {
        targetx = x;
        targety = y;
        this.actor = actor;
    }
}
