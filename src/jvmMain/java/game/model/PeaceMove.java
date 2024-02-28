package game.model;

import org.jetbrains.annotations.NotNull;

public class PeaceMove extends Move {
    public PeaceMove(int x, int y, @NotNull Piece actor) {
        super(x, y, actor);
    }
}
