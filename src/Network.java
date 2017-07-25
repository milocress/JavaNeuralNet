/**
 */
public class Network {
    public int[] architecture;
    public int depth;
    public float fitness;
    public float probability;
    public String pedigree = "Original";
    Layer[] layer;
    public Network(int[] architecture, int inputSize) {
        depth = architecture.length;
        this.architecture = architecture;
        layer = new Layer[depth];
        layer[0] = new Layer(inputSize, architecture[0]);
        for (int i = 1; i < depth; i++) {
            layer[i] = new Layer(architecture[i-1], architecture[i]);
        }

    }

    public Network(float[][][] chromosome, float[][] input) {
        depth = chromosome.length;
        layer = new Layer[depth];
        layer[0] = new Layer(input[0].length, chromosome[0]);
        for (int i = 1; i < depth; i++) {
            layer[i] = new Layer(chromosome[i-1].length, chromosome[i]);
        }

    }

    public float[][] evaluate(float[][] input) {
        layer[0].evaluateLayer(input);
        for (int i = 1; i < depth; i++) {
            layer[i].evaluateLayer(layer[i-1].output);
        }
        return layer[depth-1].output;
    }

    public float[][][] getWeights() {
        float[][][] weights = new float[depth][][];
        for (int i = 0; i < depth; i++) {
            weights[i] = layer[i].getLayerWeights();
        }
        return weights;
    }

}
