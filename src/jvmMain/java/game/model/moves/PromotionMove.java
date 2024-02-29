package game.model.moves;

import game.model.Piece;
import game.model.PieceType;
import org.jetbrains.annotations.NotNull;

import static game.model.Position.pos;

public class PromotionMove extends Move {
    public Piece promoted;

    public PromotionMove(int x, int y, @NotNull Piece actor, MoveMode mode, PieceType promoted) {
        super(x, y, actor, mode);
        this.promoted = new Piece(actor.color, promoted,pos(x,y));
    }
}
