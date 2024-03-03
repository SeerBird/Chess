package game.simpleBots;

import game.Choice;
import game.MoveFuture;
import game.MoveGenerator;
import game.model.Board;

import java.util.ArrayList;

public class Whacky implements MoveGenerator {
    @Override
    public MoveFuture selectFuture(ArrayList<Board> futures) {
        return new MoveFuture(new Choice((int) (Math.random() * futures.size())));
    }

    @Override
    public void endGame(int victory) {

    }
}
