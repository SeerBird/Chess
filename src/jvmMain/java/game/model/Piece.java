package game.model;

public class Piece {
    public final Color color;
    public final PieceType type;
    public int x;
    public int y;

    public Piece(Color color, PieceType type, int x, int y) {
        this.color = color;
        this.type = type;
    }
}
