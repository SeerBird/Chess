package game.model;

public class Piece {
    public final boolean color;
    public final PieceType type;
    public Position pos;

    /**
     * @param color {@code true} for black
     */

    public Piece(boolean color, PieceType type, Position pos) {
        this.color = color;
        this.type = type;
        this.pos = pos;
    }
    public Piece(Piece actor, Position pos) {
        this.color = actor.color;
        this.type = actor.type;
        this.pos = pos;
    }
}
