package game;

import game.ML.MinMaxer;
import game.model.Board;
import game.model.moves.Move;
import game.output.GameWindow;
import game.output.Renderer;
import game.output.ui.Menu;
import game.util.DevConfig;
import game.util.Logging;
import game.util.Maths;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;


public class GameHandler {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    static final GameWindow window = new GameWindow();
    static final ArrayList<Board> boardHistory = new ArrayList<>();
    static MoveGenerator black;
    static MoveGenerator white;
    static long lastTimedGame;

    public static void start() throws ExecutionException, InterruptedException {
        //region connect MoveGenerators
        black = new MinMaxer();
        white = new Menu();
        if (DevConfig.randomStart && Math.random() > 0.5) {
            swapMoveGenerators();
        }
        //endregion
        //region setup
        Logging.setup();
        boardHistory.add(new Board());
        boardHistory.get(0).reset();
        lastTimedGame = 0;
        out();
        //endregion
        for (int gameCount = 0; true; gameCount++) {
            //region helper locals
            MoveGenerator player;
            ArrayList<Board> futures;
            int choice;
            //endregion
            for (int turn = 0; ; turn++) {
                //region set this turn's player
                if (turn % 2 == 1) {
                    player = black;
                } else {
                    player = white;
                }
                //endregion
                futures = getBoard().getPossibleMoves();
                //region break out of the loop if the game should end
                if (futures == null || boardHistory.size() > DevConfig.turnLimit) {
                    //region draw
                    player.endGame(0);
                    getOpponent(player).endGame(0);
                    break;
                    //endregion
                } else if (futures.isEmpty()) {
                    //region lose
                    player.endGame(-1);
                    getOpponent(player).endGame(1);
                    break;
                    //endregion
                }
                //endregion
                choice = player.selectFuture(futures).get();
                boardHistory.add(futures.get(choice));
            }
            //region MoveGenerator-independent output
            if (gameCount == DevConfig.mandatoryOutputPeriod) {
                out();
                gameCount = 0;
                logger.info(Maths.round(DevConfig.mandatoryOutputPeriod / ((System.nanoTime() - lastTimedGame) / Math.pow(10, 9)), 1)
                        + " games per second.");
                lastTimedGame = System.nanoTime();
            }
            //endregion
            //region start a new game
            swapMoveGenerators();
            boardHistory.clear();
            boardHistory.add(new Board());
            boardHistory.get(0).reset();
            //endregion
        }
    }

    public static void out() {
        Board savedBoard = getBoard();
        Thread render = new Thread(() -> {
            synchronized (window) {
                Renderer.drawImage(window.getCanvas(), savedBoard);
                window.showCanvas();
            }
        });
        render.start();
    }

    private static void swapMoveGenerators() {
        MoveGenerator temp = black;
        black = white;
        white = temp;
    }

    private static MoveGenerator getOpponent(MoveGenerator player) {
        if (player == black) {
            return white;
        }
        return black;
    }

    public static Board getBoard() {
        return boardHistory.get(boardHistory.size() - 1);
    }
}
