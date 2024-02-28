package game.model;

import org.jetbrains.annotations.NotNull;

public class KingData {
    public boolean kingMoved;
    public boolean rook0Moved;
    public boolean rook7Moved;
    public Piece king;

    public KingData(Piece king) {
        kingMoved = false;
        rook0Moved = false;
        rook7Moved = false;
        this.king = king;
    }

    public KingData(@NotNull KingData last, Piece king) {
        kingMoved = last.kingMoved;
        rook7Moved = last.rook7Moved;
        rook0Moved = last.rook0Moved;
        this.king = king;
    }
}
