/**
 */
public class Layer {
    public int previousLayerSize;
    public Neuron[] neuronLayer;
    public float[][] output;

    public Layer (int previousLayerSize, int layerSize) {
        this.previousLayerSize = previousLayerSize;
        neuronLayer = new Neuron[layerSize];
        for (int i = 0; i < layerSize; i++) {
            neuronLayer[i] = new Neuron(previousLayerSize);
        }
    }

    public Layer (int previousLayerSize, float[][] weights) {
        this.previousLayerSize = previousLayerSize;
        neuronLayer = new Neuron[weights.length];
        for (int i = 0; i < weights.length; i++) {
            neuronLayer[i] = new Neuron(weights[i]);
        }

    }

    public float[][] evaluateLayer(float input[][]) {
        float[][] activation = new float[input.length][neuronLayer.length];
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < neuronLayer.length; j++) {
                activation[i][j] = neuronLayer[j].evaluate(input[i]);
            }
        }
        output = activation;
        return activation;
    }

    public float[][] getLayerWeights() {
        float[][] layerWeight = new float[neuronLayer.length][previousLayerSize + 1];
        for (int i = 0; i < neuronLayer.length; i++) {
            for (int j = 0; j < neuronLayer[i].getWeight().length; j++) {
                layerWeight[i][j] = neuronLayer[i].getWeight()[j];
            }
        }
        return layerWeight;
    }

}