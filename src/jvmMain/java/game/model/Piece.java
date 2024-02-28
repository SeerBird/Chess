package game.model;

public class Piece {
    public final boolean color;
    public final PieceType type;
    public int x;
    public int y;

    /**
     * @param color {@code true} for black
     */

    public Piece(boolean color, PieceType type, int x, int y) {
        this.color = color;
        this.type = type;
        this.x=x;
        this.y=y;
    }
    public Piece(Piece actor, int x, int y) {
        this.color = actor.color;
        this.type = actor.type;
        this.x=x;
        this.y=y;
    }
}
