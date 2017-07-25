import java.util.List;

/**
 */
public class Trainer {
    public int size;
    public Network[] genePool;
    public int[] architecture;
    public float[][] input; // The first index refers to the first set of inputs (to be associated with a certain set of outputs), while the second refers to individual nodes.
    public float[][] bestOutput;
    public int bestIndex = 0;
    /*

    This class generates an ecosystem which selects nets which display favorable traits, but it is the responsibility of the calling function to determine the fitness of these nets.

     */

    public Trainer(int[] architecture, float[][] input, int genePoolSize) {
        genePool = new Network[genePoolSize];
        this.size = genePoolSize;
        this.architecture = architecture;
        this.input = input;
        for (int i = 0; i < size; i++) {
            genePool[i] = new Network(architecture, input[0].length); // input[0].length is the number of input nodes that exist for any network.
        }
        evaluate();
        bestOutput = genePool[0].evaluate(input);
    }

    public float[][][] evaluate() {
        float[][][] output = new float[size][][];
        for (int i = 0; i < size; i++) {
            output[i] = genePool[i].evaluate(input);
        }
        return output;
    }

    public float[][][] evaluate(float[][] input) { //This is for evaluating test data, as opposed to training data.
        float[][][] output = new float[size][][];
        for (int i = 0; i < size; i++) {
            output[i] = genePool[i].evaluate(input);
        }
        return output;
    }

    public float[][][] evolve(List fittest, float[] meanSquaredError) { //The fittest array is a list provided by the caller of the genetic material best suited for the task, fittest[0] being the best.
        /*
        * I am using roulette wheel selection and random crossover for mating, and mutation to increase hill climbing.
        */
        Network[] nextGen = new Network[genePool.length];
        float fitnessSum = 0;
        float probabilitiesSum = 0;
        for (int i = 0; i < meanSquaredError.length; i++) {
            genePool[i].fitness = (float)Math.abs(1 / meanSquaredError[i] + 0.03);
            fitnessSum+=genePool[i].fitness;
        }
        for (int i = 0; i < meanSquaredError.length; i++) {
            genePool[i].probability = probabilitiesSum + genePool[i].fitness/fitnessSum;
            probabilitiesSum+=genePool[i].fitness/fitnessSum;

        }
        for (int count = 0; count < nextGen.length; count+=2) {
            Network[] parent = select();

            if (Math.random() < 0.7) { // Making it so that offspring are always created. This is just a debugging fudge, DON'T KEEP THIS!
                while (parent[1] == null || parent[0] == null) {
                    parent = select();                          //I've been getting errors that they are null a small fraction of the time. If they are, then now th process repeats and I need not worry.
                }
                Network[] children = mate(parent[0], parent[1]);
                nextGen[count] = children[0];
                nextGen[count + 1] = children[1];

            }
            else {
                while (parent[0] == null || parent[1] == null) {
                    parent = select();
                }
                nextGen[count] = parent[0];
                nextGen[count+1] = parent[1];
            }
        }

        for (int i = 0; i < nextGen.length; i++) {
            nextGen[i] = mutate(nextGen[i], meanSquaredError[i], (float)0.03);
        }
        bestOutput = genePool[(int)fittest.get(0)].evaluate(input);
        bestIndex = (int)fittest.get(0);
        genePool = nextGen;
        return evaluate();

        /*
        int mostFit = (int)fittest.get(0);
        //genePool[(int)fittest.get(fittest.size()-2)] = mutate(genePool[mostFit], meanSquaredError[mostFit]);
        //genePool[(int)fittest.get(fittest.size()-2)].pedigree = "clone";
        int leastFit = (int)fittest.get(fittest.size() - 1);
        fittest.remove(0);
        fittest.remove(fittest.size() - 1);
        int randomMate = (int)Math.random()* genePool.length;
        genePool[randomMate] = mate(genePool[mostFit], genePool[randomMate]);
        for (int i = 0; i < fittest.size(); i++) {
            genePool[(int)fittest.get(i)] = mutate(genePool[(int)fittest.get(i)], meanSquaredError[(int)fittest.get(i)], mutateFactor);
            genePool[(int)fittest.get(i)].pedigree = "mutant";
        }
        bestOutput = genePool[mostFit].evaluate(input);
        genePool[mostFit] = mutate(genePool[mostFit], meanSquaredError[mostFit], 0.0001);
        bestIndex = mostFit;
        return evaluate();
        */
    }

    public Network[] mate(Network a, Network b) {


        float[][][] chromosomeA = a.getWeights();
        float[][][] chromosomeB = b.getWeights();
        float[][][] childChromosomeA = chromosomeA.clone();
        float[][][] childChromosomeB = chromosomeA.clone();
        for (int i = 0; i < chromosomeA.length; i++) {
            for (int j = 0; j < chromosomeA[i].length; j++) {
                for (int k = 0; k < chromosomeA[i][j].length; k++) {
                    int rand = (int)(Math.random() * 2 );

                    if (rand == 0) {
                        childChromosomeA[i][j][k] = chromosomeA[i][j][k]; // 1/2 chance to be one weight
                        childChromosomeB[i][j][k] = chromosomeB[i][j][k];
                    }
                    else if (rand == 1) {
                        childChromosomeA[i][j][k] = chromosomeB[i][j][k]; // 1/2 chance to be the other weight
                        childChromosomeB[i][j][k] = chromosomeA[i][j][k];
                    }

                }
            }
        }
        Network[] children = {new Network(childChromosomeA, input), new Network(childChromosomeB, input)};
        children[0].pedigree = "child";
        children[1].pedigree = "child";
        return children;
    }

    public void setInput(float[][] input) {
        this.input = input;
    }

    public Network mutate(Network a, float meanSquaredError, float mutationFactor) {
        float[][][] chromosome =  a.getWeights();
        for (int i = 0; i < chromosome.length; i++) {
            for (int j = 0; j < chromosome[i].length; j++) {
                for (int k = 0; k < chromosome[i][j].length; k++) {
                    int rand = (int) (Math.random() * (1/mutationFactor));
                    if (rand == 0) {
                        chromosome[i][j][k] += (Math.random() * 0.2 - 0.1) * meanSquaredError;
                    }
                }
            }
        }
        Network mutant = new Network(chromosome, input);
        mutant.pedigree = "mutant";
        return mutant;
    }
    public Network[] select() {
        Network[] parent = new Network[2];
        for (int i = 0; i < 2; i++) {
            float rand = (float) Math.random();
            for (int j = 0; j < genePool.length - 1; j++) {
                if (rand >= genePool[j].probability && rand < genePool[j + 1].probability) {
                    parent[i] = genePool[j];
                    break;
                }
            }
            if (rand > genePool[genePool.length-1].probability) {
                parent[i] = genePool[genePool.length-1];
            }
        }
        return parent;
    }
}
