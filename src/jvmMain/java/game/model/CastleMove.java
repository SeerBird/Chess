package game.model;

import org.jetbrains.annotations.NotNull;

public class CastleMove extends PeaceMove{
    public CastleMove(int destx, int desty, @NotNull Piece actor) {
        super(destx, desty, actor);
    }
}
