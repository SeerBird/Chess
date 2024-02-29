package game;

import game.ML.Whacky;
import game.model.Board;
import game.output.GameWindow;
import game.output.Renderer;
import game.output.ui.Menu;
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
    static final Queue<Long> lastGames = new LinkedBlockingDeque<>(100);
    static MoveGenerator black;
    static MoveGenerator white;
    static int maxTurns = 0;

    public static void start() {
        //region setup
        Logging.setup();
        for (int i = 0; i < 99; i++) {
            lastGames.add(0L);
        }
        //endregion
        for (int gameCount = 0; true; gameCount++) {
            //region start a game
            boardHistory.clear();
            boardHistory.add(new Board());
            boardHistory.get(0).reset();
            black = new Menu();
            white = new Menu();
            //endregion
            //region helper locals
            MoveGenerator player;
            ArrayList<Board> futures;
            int choice;
            boolean turnColor;
            //endregion
            for (int i = 0; ; i++) {
                if (i == 100000) {
                    i = 0;
                }
                //region set current player
                turnColor = i % 2 == 1;
                if (turnColor) {
                    player = black;
                } else {
                    player = white;
                }
                //endregion
                futures = getBoard().getPossibleMoves(turnColor);
                if(boardHistory.size()>100000){
                    logger.severe("wot");
                }
                if (futures.isEmpty()) {
                    //region end game
                    player.endGame(false);
                    getOpponent(player).endGame(true);
                    lastGames.add(System.nanoTime());
                    long time = lastGames.remove();
                    if (boardHistory.size() > maxTurns) {
                        maxTurns = boardHistory.size();
                    }
                    if (gameCount == 100) {
                        out();
                        gameCount = 0;
                        logger.info(Maths.round(99 / ((System.nanoTime() - time) / Math.pow(10, 9)),1) + " games per second, " +
                                Maths.round(Runtime.getRuntime().freeMemory() / Math.pow(2, 20), 1) + " mb free, max " +
                                maxTurns + " turns");
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
