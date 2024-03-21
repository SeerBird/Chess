package game.ML;

import java.util.ArrayList;

class Turn {
    public ArrayList<double[][]> potentialVectors;
    public double[][] vectors;

    public Turn() {
        potentialVectors = new ArrayList<>();
    }

    public void choose(int id) {
        vectors = potentialVectors.get(id);
        potentialVectors.clear();
    }
    public double output(){
        return vectors[vectors.length-1][0];
    }
}
