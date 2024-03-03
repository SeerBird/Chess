package game.ML;

import game.MoveFuture;
import game.MoveGenerator;
import game.model.Board;

import java.util.ArrayList;

public class Learner implements MoveGenerator {
    @Override
    public MoveFuture selectFuture(ArrayList<Board> futures) {
        return null;
    }

    @Override
    public void endGame(int victory) {

    }
}
