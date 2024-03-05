package game;

import game.ML.Learner;
import game.model.Board;
import game.output.GameWindow;
import game.output.Renderer;
import game.simpleBots.MinMaxer;
import game.util.DevConfig;
import game.util.Logging;
import game.util.Maths;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;


public class GameHandler {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    static final GameWindow window = new GameWindow();
    static Board board;
    static MoveGenerator black;
    static MoveGenerator white;
    static long lastTimedGame;

    public static void start() throws ExecutionException, InterruptedException {
        //region connect MoveGenerators
        black = new Learner();
        white = new Learner();
        double learnerWins = 0;
        if (DevConfig.randomStart && Math.random() > 0.5) {
            swapMoveGenerators();
        }
        //endregion
        //region setup
        Logging.setup();
        board = new Board();
        board.reset();
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
                //region set this choice's player
                if (turn % 2 == 1) {
                    player = black;
                } else {
                    player = white;
                }
                //endregion
                futures = getBoard().getPossibleMoves();
                //region break out of the loop if the game should end
                if (futures == null || turn > DevConfig.turnLimit) {
                    //region draw
                    player.endGame(GameEnd.draw);
                    getOpponent(player).endGame(GameEnd.draw);
                    break;
                    //endregion
                } else if (futures.isEmpty()) {
                    //region lose
                    player.endGame(GameEnd.loss);
                    getOpponent(player).endGame(GameEnd.victory);
                    if(getOpponent(player) instanceof Learner){
                        learnerWins++;
                    }
                    break;
                    //endregion
                }
                //endregion
                choice = player.selectFuture(futures).get();
                board = futures.get(choice);
            }
            //region MoveGenerator-independent output
            if (gameCount == DevConfig.mandatoryOutputPeriod) {
                out();
                gameCount = 0;
                logger.info(Maths.round(DevConfig.mandatoryOutputPeriod / ((System.nanoTime() - lastTimedGame) / Math.pow(10, 9)), 1)
                        + " games per second. Learner winrate: "+learnerWins/DevConfig.mandatoryOutputPeriod);
                learnerWins=0;
                lastTimedGame = System.nanoTime();
            }
            //endregion
            //region start a new game
            swapMoveGenerators();
            board = new Board();
            board.reset();
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
        return board;
    }
}
