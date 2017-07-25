import com.sun.deploy.util.ArrayUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 */
public class Tester {
    int[] a;
    int[] b;
    int[] c;
/*
    public static void main(String args[]) {


        // a simple "test drive" of the Neural network training algorithm. The input is a few sets of bits {1, 0, 1, 1}, {0, 0, 0, 0}, and {1, 1, 1, 1} which the output will be trained to match.



        int[] architecture = {1, 4};

        double[][] trainingData = {
                {1, 0, 1, 1},
                {0, 0, 0, 0},
                {1, 1, 1, 1}
        };

        double[][] optimalOutputData = {
                {1, 0, 1, 1},
                {0, 0, 0, 0},
                {1, 1, 1, 1}
        };


        int genePoolSize = 10;


        Trainer trainer = new Trainer(architecture, trainingData, genePoolSize);
        double[][][] output = trainer.evaluate();

        List fittest = new ArrayList();


        for (int counter = 0; counter < 100; counter++) {
            List networkError = new ArrayList();
            List error = new ArrayList();
            for (int i = 0; i < output.length; i++) {
                //Total error of each training input;
                double sigma = 0;
                for (int j = 0; j < output[i].length; j++) {
                    for (int k = 0; k < output[i][j].length; k++) {
                        sigma += Math.abs(optimalOutputData[j][k] - output[i][j][k]); // Calculates the error of the network
                    }
                }
                networkError.add(i, sigma);
                error.add(i, sigma);
            }

            for (int i = 0; i < genePoolSize; i++) {
                fittest.add(i, networkError.indexOf(Collections.min(networkError)));
                networkError.remove((int)fittest.get(i));
                networkError.add((int)fittest.get(i), Double.POSITIVE_INFINITY);
            }

            output = trainer.train(fittest);
            System.out.println(Collections.min(error));
        }
        System.out.print("Done");

/*
        double[] input = {1.0};
        int[] architecture = {1, 2, 2, 1};


        Network network = new Network(architecture, input);
        for (int i = 0; i < architecture[architecture.length - 1]; i++) {
            System.out.println(network.evaluate()[i]);

        }
        double[][][] chromosome = network.getWeights();

        Network test = new Network(chromosome, input);
        System.out.println(test.evaluate()[0]);

/*
        double[] inputLayer = new double[3];
        Neuron neuron = new Neuron(inputLayer.length);
        printNeuronWeights(neuron);
        inputLayer = makeInputs(inputLayer);
        double activation = neuron.evaluate(inputLayer);
        //System.out.println(activation);
        Layer layer = new Layer(inputLayer, 3);
        double[][] layerWeights = layer.getLayerWeights();
        System.out.println(layerWeights);

    }
    public static void printNeuronWeights(Neuron neuron) {
        for (int i = 0; i < neuron.getWeight().length; i++) {
            //System.out.println(neuron.getWeight()[i]);
        }
    }
    public static double[] makeInputs(double[] inputLayer) {
        for (int i = 0; i < inputLayer.length; i++) {
            inputLayer[i] = Math.random();
        }
        return inputLayer;
*/
    //}

}
