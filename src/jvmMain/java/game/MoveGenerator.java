package game;

import game.model.Board;

import java.util.ArrayList;

public interface MoveGenerator {

    /**
     *
     * @param futures a non-empty list of accessible board states to choose from
     * @return the index of the future chosen
     */
    public MoveFuture selectFuture(ArrayList<Board> futures);
    public void endGame(GameEnd gameEnd);
}
