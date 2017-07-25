
/**
 */
public class Neuron {
    public float weight[];

    public Neuron(int weightsLength) {
        weight = new float[weightsLength + 1];
        for (int i = 0; i < weightsLength + 1; i++) { //the last weight is actually a bias, it's just easier to set it as a weight...
            weight[i] = (float)(Math.random()*2) - 1;
        }
    }

    public Neuron(float[] weights) {
        weight = weights;

    }

    public float evaluate(float[] input) {
        float sigma = 0;
        for (int i = 0; i < input.length; i++) {
            sigma+= input[i] * weight[i];
        }
        sigma+= weight[input.length]; // This adds the bias to the expression.
        return sigmoid(sigma);
    }

    public float sigmoid (float z) {
        return (float)(1 / (1 + Math.exp(-1 * z)));
    }

    public float[] getWeight() {

        return weight;
    }


}
