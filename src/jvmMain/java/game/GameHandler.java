package game;

import game.model.Board;
import game.output.GameWindow;
import game.output.Renderer;
import game.output.ui.Menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;


public class GameHandler {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    static final GameWindow window = new GameWindow();
    static final ArrayList<Board> history = new ArrayList<>();
    static MoveGenerator black;
    static MoveGenerator white;

    public static void out() {
        Renderer.drawImage(window.getCanvas());
        window.showCanvas();
    }

    public static void start() {
        history.clear();
        history.add(new Board());
        history.get(0).reset();
        black = new Menu();
        white = new Menu();
        MoveGenerator player;
        ArrayList<Board> futures;
        int choice = -1;
        for (int i = 0; ; i++) {
            if (i % 2 == 0) {
                player = white;
            } else {
                player = black;
            }
            futures = getBoard().getPossibleMoves(i % 2 == 1);
            try {
                choice = player.selectFuture(futures).get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (choice == -2) {
                player.endGame(false);
                getOpponent(player).endGame(true);
                break;
            }
            history.add(futures.get(choice));
        }
    }

    private static MoveGenerator getOpponent(MoveGenerator player) {
        if (player == black) {
            return white;
        }
        return black;
    }

    public static Board getBoard() {
        return history.get(history.size() - 1);
    }
}
