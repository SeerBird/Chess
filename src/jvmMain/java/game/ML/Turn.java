package game.ML;

import java.util.ArrayList;

class Turn {
    public double confidence;
    public ArrayList<double[][]> potentialVectors;
    public double[][] vectors;

    public Turn() {
        potentialVectors = new ArrayList<>();
    }

    public void choose(int id,double confidence) {
        vectors = potentialVectors.get(id);
        this.confidence=confidence;
        potentialVectors.clear();
    }
    public double output(){
        return vectors[vectors.length-1][0];
    }
}
