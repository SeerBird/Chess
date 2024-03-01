package game;

import game.ML.Whacky;
import game.model.Board;
import game.output.GameWindow;
import game.output.Renderer;
import game.output.ui.Menu;
import game.util.DevConfig;
import game.util.Logging;
import game.util.Maths;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Logger;


public class GameHandler {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    static final GameWindow window = new GameWindow();
    static final ArrayList<Board> boardHistory = new ArrayList<>();
    static MoveGenerator black;
    static MoveGenerator white;
    static int maxTurns = 0;
    static long lastTimedGame = 0;

    public static void start() {
        //region connect MoveGenerators
        black = new Menu();
        white = new Menu();
        //endregion
        //region setup
        Logging.setup();
        boardHistory.add(new Board());
        boardHistory.get(0).reset();
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
                if (futures.isEmpty() || boardHistory.size() > DevConfig.turnLimit) {
                    if (boardHistory.size() > DevConfig.turnLimit) {
                        player.endGame(0);
                        getOpponent(player).endGame(0);
                    } else {
                        player.endGame(-1);
                        getOpponent(player).endGame(1);
                        if (boardHistory.size() > maxTurns) {
                            maxTurns = boardHistory.size();
                        }
                    }
                    //region end game
                    if (gameCount == 100) {
                        out();
                        gameCount = 0;
                        logger.info(Maths.round(100 / ((System.nanoTime() - lastTimedGame) / Math.pow(10, 9)), 1)
                                + " games per second, " +
                                Maths.round(Runtime.getRuntime().freeMemory() / Math.pow(2, 20), 1)
                                + " mb free, max " +
                                maxTurns
                                + " turns");
                        lastTimedGame = System.nanoTime();
                    }
                    break;
                    //endregion
                }
                try {
                    choice = player.selectFuture(futures).get();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                boardHistory.add(futures.get(choice));
            }
            //region start a new game
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
