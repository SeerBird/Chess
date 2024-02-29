package game.model.moves;

import game.model.Piece;
import org.jetbrains.annotations.NotNull;

public class CastleMove extends Move {
    public CastleMove(int destx, int desty, @NotNull Piece actor) {
        super(destx, desty, actor, MoveMode.peaceful);
    }
}
