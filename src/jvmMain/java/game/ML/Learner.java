package game.ML;

import game.Choice;
import game.MoveFuture;
import game.MoveGenerator;
import game.Resources;
import game.model.Board;
import game.model.KingData;
import game.model.Piece;
import game.util.DevConfig;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;

import static game.util.DevConfig.layerWidth;
import static game.util.DevConfig.layers;

public class Learner implements MoveGenerator {
    public static ArrayList<double[][]> weights;
    public static final int inputSize = 8 * 8 * 12 + 3;

    static {
        try {
            if (Resources.weights == null) {
                throw new Exception("noooo");
            }
            FileInputStream fis = new FileInputStream(new File(Resources.weights.toURI()));
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object input = ois.readObject();
            weights = (ArrayList<double[][]>) input;
        } catch (Exception e) {
            weights = new ArrayList<>();
            weights.add(new double[inputSize][layerWidth]);
            for (int i = 0; i < DevConfig.layers; i++) {
                weights.add(new double[layerWidth][layerWidth]);
            }
            weights.add(new double[1][layerWidth]);
            saveWeights();
        }
    }

    @Override
    public MoveFuture selectFuture(ArrayList<Board> futures) {
        ArrayList<Double> scores = new ArrayList<>(futures.size());
        double total = 0;
        for (int i = 0; i < futures.size(); i++) {
            scores.add(getScore(futures.get(0)));
            total += scores.get(i);
        }
        if (total != 0) {
            double random = Math.random();
            //region normalize scores
            for (int i = 0; i < scores.size(); i++) {
                scores.set(i, scores.get(i) / total);
            }
            //endregion
            //region return choice with probability proportional to its score
            for (int i = 0; i < scores.size(); i++) {
                random -= scores.get(i);
                if (random < 0) {
                    return new MoveFuture(new Choice(i));
                }
            }
            //endregion
        }
        return new MoveFuture(new Choice((int) (Math.random() * futures.size())));
    }

    private static double getScore(@NotNull Board board) {
        int id;
        boolean color = board.lastMove.actor.color;
        double[] vector = new double[inputSize];
        for (Piece piece : board.getPieces()) {
            //region get friend/foe+type id from 0 to 11
            id = 5;
            switch (piece.type) {
                case pawn -> id = 0;
                case knight -> id = 1;
                case bishop -> id = 2;
                case rook -> id = 3;
                case queen -> id = 4;
            }
            if (piece.color == color) { // friendly piece
                id += 6;
            }
            //endregion
            vector[id + 11 * piece.pos.x + 7 * 11 * piece.pos.y] = 1;
        }
        KingData data = board.getKingData(color);
        if (!(data.rook7Moved || data.kingMoved)) {
            vector[inputSize - 3] = 1;
        }
        if (!(data.rook7Moved || data.kingMoved)) {
            vector[inputSize - 2] = 1;
        }
        vector[inputSize - 1] = (double) (board.pawnDirection(color) + 1) / 2;
        double[][] matrix;
        double[] temp;
        double value;
        for (int i = 0; i < layers + 2; i++) {
            matrix = weights.get(i);
            temp = new double[matrix.length];
            for (int j = 0; j < matrix.length; j++) {
                value = 0;
                for (double weight : matrix[j]) {
                    value += weight * vector[j];
                }
                temp[j] = value;
            }
            vector = temp;
        }
        return Math.pow(2, vector[0]);
    }

    private static void saveWeights() {
        try (FileOutputStream fos = new FileOutputStream("src/jvmMain/resources/game/" + Resources.weightsFileName)) {
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(weights);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void endGame(int victory) {
        Thread thread = new Thread(() -> {
        });
        thread.start();
    }
}
