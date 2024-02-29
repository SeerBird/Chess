package game.output.ui;

import game.Choice;
import game.GameHandler;
import game.MoveFuture;
import game.MoveGenerator;
import game.model.Board;
import game.model.Piece;
import game.model.Position;
import game.model.moves.MoveMode;
import game.model.moves.PromotionMove;
import game.output.ui.rectangles.Button;
import game.output.ui.rectangles.PromotionButton;
import game.util.DevConfig;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static game.util.DevConfig.tileSize;

public class Menu implements MoveGenerator {
    private static IElement pressed;
    private static final ArrayList<Button> pieces = new ArrayList<>();
    private static ArrayList<Button> moves;
    private static ArrayList<PromotionButton> promotions;
    private static Focusable focused;
    private static final Choice choice = new Choice(-1);

    public static ArrayList<PromotionButton> getPromotions() {
        return promotions;
    }

    @Override
    public MoveFuture selectFuture(@NotNull ArrayList<Board> futures) {
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
                Map<Position, ArrayList<Board>> groupedPromotions = new HashMap<>();
                for (Board future : futures) {
                    if (future.lastMove.actor == actor) {
                        if (!(future.lastMove instanceof PromotionMove)) {
                            moves.add(new Button(future.lastMove.dest.x * tileSize, future.lastMove.dest.y * tileSize,
                                    tileSize, tileSize, () -> {
                                choice.value = futures.indexOf(future);
                                synchronized (choice) {
                                    choice.notify();
                                }
                                moves = null;
                                GameHandler.out();
                            }, future.lastMove.mode == MoveMode.capture
                                    ? DevConfig.red : DevConfig.green));
                        } else {
                            groupedPromotions.computeIfAbsent(future.lastMove.dest, k -> new ArrayList<>());
                            groupedPromotions.get(future.lastMove.dest).add(future);
                        }
                    }
                }
                if (!groupedPromotions.isEmpty()) {
                    for (Position dest : groupedPromotions.keySet()) {
                        moves.add(new Button(dest.x * tileSize, dest.y * tileSize, tileSize, tileSize, () -> {
                            promotions = new ArrayList<>();
                            for (int i = 0; i < groupedPromotions.get(dest).size(); i++) {
                                Board future = groupedPromotions.get(dest).get(i);
                                promotions.add(new PromotionButton(tileSize*2 + i * tileSize, (int) (3.5 * tileSize),
                                        tileSize, tileSize, () -> {
                                    choice.value = futures.indexOf(future);
                                    synchronized (choice) {
                                        choice.notify();
                                    }
                                    promotions = null;
                                    moves = null;
                                    GameHandler.out();
                                }, DevConfig.promotion, (PromotionMove) (future.lastMove)));
                            }
                            GameHandler.out();
                        }, groupedPromotions.get(dest).get(0).lastMove.mode == MoveMode.capture ? DevConfig.red : DevConfig.green));
                    }
                }
                GameHandler.out();
            }, DevConfig.green));
        }
        GameHandler.out();
        choice.value = -1;
        return new MoveFuture(choice);
    }

    public static boolean press(ArrayRealVector pos) {
        if (promotions != null) {
            for (Button promotion : promotions) {
                if (promotion.press(pos)) {
                    pressed = promotion;
                    GameHandler.out(); // to draw the button being pressed?
                    return true;
                }
            }
            promotions = null;
            GameHandler.out();
        } else if (moves != null) {
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
    public void endGame(boolean victory) {

    }
}
