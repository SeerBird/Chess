package game.ML;

import game.Choice;
import game.MoveFuture;
import game.MoveGenerator;
import game.model.Board;
import game.model.Piece;
import game.model.PieceType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MinMaxer implements MoveGenerator {
    @Override
    public MoveFuture selectFuture(ArrayList<Board> futures) {
        int maxScore = Integer.MIN_VALUE;
        ArrayList<Integer> scores = new ArrayList<>();
        for (int i = 0; i < futures.size(); i++) {
            int score = 0;
            for (Piece piece : futures.get(i).getPieces()) {
                if (piece.color) {
                    score += score(piece.type);
                } else {
                    score -= score(piece.type);
                }
            }
            if (!futures.get(i).lastMove.actor.color) {
                score *= -1;
            }
            scores.add(score);
            if(score>maxScore){
                maxScore=score;
            }
        }
        ArrayList<Integer> ids = new ArrayList<>();
        for(int i=0;i<scores.size();i++){
            if(scores.get(i)==maxScore){
                ids.add(i);
            }
        }
        return new MoveFuture(new Choice(ids.get((int) (Math.random()*ids.size()))));
    }

    private int score(@NotNull PieceType type) {
        switch (type) {
            case pawn -> {
                return 1;
            }
            case knight, bishop -> {
                return 3;
            }
            case rook -> {
                return 5;
            }
            case queen -> {
                return 9;
            }
        }
        return 0;
    }

    @Override
    public void endGame(int victory) {

    }
}
