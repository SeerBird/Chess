package game.ML;

import game.*;
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
    public static final double[][][] weights;
    public static final int inputSize = 8 * 8 * 12 + 4;
    final ArrayList<Turn> history; // for each choice, there are 5 vector outputs.

    public Learner() {
        history = new ArrayList<>();
    }

    static {
        double[][][] temp;
        try {
            if (Resources.weights == null) {
                throw new Exception("noooo");
            }
            FileInputStream fis = new FileInputStream(new File(Resources.weights.toURI()));
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object input = ois.readObject();
            temp = (double[][][]) input;
        } catch (Exception e) {
            temp = new double[layers + 2][][];
            temp[0] = new double[layerWidth][inputSize];
            for (int i = 0; i < DevConfig.layers; i++) {
                temp[i + 1] = new double[layerWidth][layerWidth];
            }
            temp[layers + 1] = new double[1][layerWidth];
            for (int layer = 0; layer < temp.length; layer++) {
                for (int i = 0; i < temp[layer].length; i++) {
                    for (int j = 0; j < temp[layer][i].length; j++)
                        temp[layer][i][j] = Math.random() * 4 - 2;
                }
            }
        }
        weights = temp;
        if (weights == null) {
            throw new RuntimeException("Failed to init weights");
        }
        Runtime.getRuntime().addShutdownHook(new Thread(Learner::saveWeights));
    }

    @Override
    public MoveFuture selectFuture(ArrayList<Board> futures) {
        history.add(new Turn());
        ArrayList<Double> scores = new ArrayList<>(futures.size());
        double total = 0;
        for (int i = 0; i < futures.size(); i++) {
            scores.add(getOdds(futures.get(0)));
            total += scores.get(i);
        }
        if (total != 0) {
            //region normalize odds
            for (int i = 0; i < scores.size(); i++) {
                scores.set(i, scores.get(i) / total);
            }
            //endregion
            //region return choice with probability proportional to its winning odds, to explore branches
            double random = Math.random();
            for (int i = 0; i < scores.size(); i++) {
                random -= scores.get(i);
                if (random < 0) {
                    getTurn().choose(i);
                    return new MoveFuture(new Choice(i));
                }
            }
            //endregion
        }
        int choice = (int) (Math.random() * futures.size());
        getTurn().choose(choice);
        return new MoveFuture(new Choice(choice));
    }

    private double getOdds(@NotNull Board board) {
        double[][] vectors = new double[layers + 3][];
        int id;
        boolean color = board.lastMove.actor.color;
        double[] vector = new double[inputSize];
        boolean mirror = board.pawnDirection(color) == -1;
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
            vector[id + 11 * piece.pos.x + 7 * 11 * (mirror ? 7 - piece.pos.y : piece.pos.y)] = 1; // repeated branching. optimize
        }
        //region add data regarding castling
        KingData data = board.getKingData(color);
        if (!(data.rook7Moved || data.kingMoved)) {
            vector[inputSize - 4] = 1;
        }
        if (!(data.rook7Moved || data.kingMoved)) {
            vector[inputSize - 3] = 1;
        }
        data = board.getKingData(!color);
        if (!(data.rook7Moved || data.kingMoved)) {
            vector[inputSize - 2] = 1;
        }
        if (!(data.rook7Moved || data.kingMoved)) {
            vector[inputSize - 1] = 1;
        }
        //endregion
        vectors[0] = vector; //input
        double[] temp;
        double value;
        for (int layer = 0; layer < layers + 2; layer++) {
            temp = new double[weights[layer].length];
            for (int i = 0; i < weights[layer].length; i++) {
                value = 0;
                for (int j = 0; j < weights[layer][i].length; j++) {
                    value += weights[layer][i][j] * vector[j];
                }
                temp[i] = activation(value);
            }
            vector = temp;
            vectors[layer + 1] = vector;
        }
        getTurn().potentialVectors.add(vectors);
        return Math.pow(2, vector[0]); // last vector consists of one value
    }

    Turn getTurn() {
        return history.get(history.size() - 1);
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

    private static double activation(double input) {
        return Math.max(0.1 * input, input);
    }

    private static double activationDerivative(double input) {
        return input < 0 ? 0.1 : 1;
    }

    @Override
    public void endGame(GameEnd gameEnd) {
        ArrayList<Turn> history = new ArrayList<>(this.history);
        Thread thread = new Thread(() -> {
            synchronized (weights) {
                double[] improvement = new double[1];
                double[] nextImprovement;
                switch (gameEnd) {
                    case victory -> improvement[0] = 1; // a higher value returned by the network is better
                    case loss -> improvement[0] = -2;
                    case draw -> improvement[0] = -1;
                }
                Turn turn;
                double[][] matrix;
                for (int pastTurn = history.size() - 1; pastTurn >= 0; pastTurn--) {
                    turn = history.get(pastTurn);
                    for (int layer = weights.length - 1; layer > 0; layer--) { // don't care about the output vector. I know the derivative from the game outcome.
                        matrix = weights[layer];
                        nextImprovement = new double[matrix[0].length];
                        for (int i = 0; i < matrix[0].length; i++) {
                            double value = 0;
                            for (int j = 0; j < matrix.length; j++) {
                                value += improvement[j] * matrix[j][i];
                                weights[layer][j][i] = weights[layer][j][i] + improvement[j] * turn.vectors[layer][j] * DevConfig.learningRate;
                            }
                            nextImprovement[i] = activationDerivative(value);
                        }
                        improvement = nextImprovement;
                    }
                }
            }

        });
        thread.start();
    }
}
