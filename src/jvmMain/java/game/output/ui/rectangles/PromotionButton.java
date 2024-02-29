package game.output.ui.rectangles;

import game.model.Piece;
import game.model.PieceType;
import game.model.Position;
import game.model.moves.PromotionMove;

import java.awt.*;

public class PromotionButton extends Button {
    public PromotionMove move;

    public PromotionButton(int x, int y, int width, int height, Runnable action, Color color, PromotionMove move) {
        super(x, y, width, height, action, color);
        this.move = move;
    }
}
