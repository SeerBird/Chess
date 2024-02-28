package game.output.ui;

import game.Choice;
import game.GameHandler;
import game.MoveFuture;
import game.MoveGenerator;
import game.model.Board;
import game.model.Piece;
import game.output.ui.rectangles.Button;
import game.util.DevConfig;
import org.apache.commons.math3.linear.ArrayRealVector;

import java.util.ArrayList;

import static game.util.DevConfig.tileSize;

public class Menu implements MoveGenerator {
    private static IElement pressed;
    private static final ArrayList<Button> pieces = new ArrayList<>();
    private static ArrayList<Button> moves;
    private static Focusable focused;
    private static final Choice choice = new Choice(-1);

    public static boolean press(ArrayRealVector pos) {
        if (moves != null) {
            for (Button move : moves) {
                if (move.press(pos)) {
                    pressed = move;
                    GameHandler.out(); // to draw the button being pressed?
                    return true;

                }
            }
            moves = null;
            GameHandler.out();
        } else {
            for (Button piece : pieces) {
                if (piece.press(pos)) {
                    pressed = piece;
                    GameHandler.out(); // to draw the button being pressed?
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean release() {
        if (pressed != null) {
            pressed.release();
            pressed = null;
            return true;
        }
        return false;
    }

    public static void focus(Focusable element) {
        focused = element;
    }

    public static void unfocus() { //I can make this multilevel. no need though.
        focused.leave();
        focused = null;
    }

    public static Focusable getFocused() {
        return focused;
    }

    public static IElement getPressed() {
        return pressed;
    }

    public static ArrayList<Button> getPieces() {
        return pieces;
    }

    public static ArrayList<Button> getMoves() {
        return moves;
    }

    @Override
    public MoveFuture selectFuture(ArrayList<Board> futures) {
        ArrayList<Piece> actors = new ArrayList<>();
        for (Board future : futures) {
            if (!actors.contains(future.lastMove.actor)) {
                actors.add(future.lastMove.actor);
            }
        }
        pieces.clear();
        for (Piece actor : actors) {
            pieces.add(new Button(actor.pos.x * tileSize, actor.pos.y * tileSize, tileSize, tileSize, () -> {
                moves = new ArrayList<>();
                for (Board future : futures) {
                    if (future.lastMove.actor == actor) {
                        moves.add(new Button(future.lastMove.dest.x * tileSize, future.lastMove.dest.y * tileSize,
                                tileSize, tileSize, () -> {
                            choice.value = futures.indexOf(future);
                            synchronized (choice) {
                                choice.notify();
                            }
                            moves = null;
                            GameHandler.out();
                        }, GameHandler.getBoard().getPiece(future.lastMove.dest.x, future.lastMove.dest.y) == null
                                ? DevConfig.green : DevConfig.red));
                    }
                }
                GameHandler.out();
            }, DevConfig.green));
        }
        GameHandler.out();
        choice.value = -1;
        return new MoveFuture(choice);
    }

    @Override
    public void endGame(boolean victory) {

    }
}
