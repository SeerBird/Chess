package game;

import game.model.Board;

import java.util.ArrayList;

public interface MoveGenerator {
    /**
     * @return -2 to give up, index of the future to choose
     */
    public MoveFuture selectFuture(ArrayList<Board> futures);
    public void endGame(int victory);
}
