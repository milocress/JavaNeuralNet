import java.util.*;

/**
 */
public class CryptoSystem {
    public static int keylength = 8;
    public static int blockLength = 8;
    public static float[][] keyedInputs;
    public static float[][] unkeyedInputs;
    public static float[][] keys;
    public static int[] aliceArchitecture = {keylength + blockLength, blockLength};
    public static int[] eveArchitecture = {blockLength, blockLength};
    public static int genePoolSize = 30;
    public static int trainingDataSize = 1000;
    public static Network encryption;
    public static Network decryption;

    public CryptoSystem(int blockLength, int keyLength){
        this.blockLength = blockLength;
        this.keylength = keyLength;

        Network[] algorithm = train(); //algorithm[0] is encryption, algorithm[1] is for decryption
        encryption = algorithm[0];
        decryption = algorithm[1];

        float[][] testData = generateTrainingData(10, blockLength);
        float[][] keys = generateTrainingData(10, keylength);
        float[][] testInput = appendKey(testData, keys);
        float[][] cipherText = round(encrypt(testData, keys[0]));
        float[][] transmittedMessage = round(decrypt(cipherText, keys[0]));
        float error = getMSE(transmittedMessage, testData);
        System.out.println("Error: " + error);
    }

    public static Network[] train() {
        float[][] image = Main.fileToFloatArray("D:\\AI\\encryptMe.bmp");
        unkeyedInputs = generateTrainingData(trainingDataSize, blockLength);
        keys = generateTrainingData(trainingDataSize, keylength);
        keyedInputs = appendKey(unkeyedInputs, keys);

        Trainer alice = new Trainer(aliceArchitecture, unkeyedInputs, genePoolSize);
        Trainer bob = new Trainer(aliceArchitecture, alice.bestOutput, genePoolSize);
        Trainer eve = new Trainer(eveArchitecture, alice.bestOutput, genePoolSize);
        float[][][] aliceOutputs = alice.evaluate();
        List aliceFitness;
        float[] aliceMSE = new float[genePoolSize];
        float[][][] bobOutputs = bob.evaluate();
        List bobFitness;
        float[] bobMSE = new float[genePoolSize];
        float[][][] eveOutputs = eve.evaluate();
        List eveFitness;
        float[] eveMSE = new float[genePoolSize];
        int loop = 0;
        float[][][] eveOfAlice = new float[genePoolSize][trainingDataSize][eveArchitecture[eveArchitecture.length - 1]];
        float[][][] bobOfAlice = new float[genePoolSize][trainingDataSize][aliceArchitecture[aliceArchitecture.length - 1]];
        for (int i = 0; i < genePoolSize; i++) {
            eveOfAlice[i] = eve.genePool[eve.bestIndex].evaluate(aliceOutputs[i]);
            bobOfAlice[i] = bob.genePool[bob.bestIndex].evaluate(aliceOutputs[i]);
            //aliceMSE[i] = (float)(-1 * getMSE(eveOfAlice[i], unkeyedInputs) + 0.5); //+ getMSE(bobOfAlice[i], unkeyedInputs);
            aliceMSE[i] = getMSE(bobOfAlice[i], unkeyedInputs) / getMSE(eveOfAlice[i], unkeyedInputs);
        }
        aliceFitness = getFitness(aliceMSE);
        for (int i = 0; i < genePoolSize; i++) {
            bobMSE[i] = getMSE(round(bobOutputs[i]), unkeyedInputs);
        }
        bobFitness = getFitness(bobMSE);
        for (int i = 0; i < genePoolSize; i++) {
            eveMSE[i] = getMSE(round(eveOutputs[i]), unkeyedInputs);
        }
        eveFitness = getFitness(eveMSE);

        System.out.println(loop + ": Alice: " + " Bob: " + (getMSE(bob.bestOutput, unkeyedInputs)) * 16 + " " + " Eve: " + (getMSE(eve.bestOutput, unkeyedInputs)) * 16 + " " + " Percent difference (should be zero): " + getMSE(bob.bestOutput, unkeyedInputs) / getMSE(eve.bestOutput, unkeyedInputs));
        if (getMSE(eve.bestOutput, unkeyedInputs) * blockLength > blockLength / 2 - 1 && getMSE(bob.bestOutput, unkeyedInputs) * blockLength < 1) {
            Network[] cryptoSystem = {alice.genePool[(int) aliceFitness.get(0)], bob.genePool[(int) bobFitness.get(0)]};
            return cryptoSystem;
        }
        //aliceOutputs = alice.evolve(aliceFitness, aliceMSE, mutateFactor);
        bob.setInput(appendKey(alice.bestOutput, keys));
        eve.setInput(alice.bestOutput);
        bobOutputs = bob.evolve(bobFitness, bobMSE);
        bob.setInput(appendKey(alice.bestOutput, keys));
        eveOutputs = eve.evolve(eveFitness, eveMSE);
        eve.setInput(alice.bestOutput);
        aliceOutputs = alice.evolve(aliceFitness, aliceMSE);
        //mutateFactor-= 0.0001;
        loop++;
        while (true) {

            for (int i = 0; i < genePoolSize; i++) {
                eveOfAlice[i] = eve.genePool[eve.bestIndex].evaluate(aliceOutputs[i]);
                bobOfAlice[i] = bob.genePool[bob.bestIndex].evaluate(aliceOutputs[i]);
                //aliceMSE[i] = (1/ getMSE(eveOfAlice[i], unkeyedInputs)); //+ getMSE(bobOfAlice[i], unkeyedInputs);
                aliceMSE[i] = getMSE(bobOfAlice[i], unkeyedInputs) / getMSE(eveOfAlice[i], unkeyedInputs);

            }
            aliceFitness = getFitness(aliceMSE);
            for (int i = 0; i < genePoolSize; i++) {
                bobMSE[i] = getMSE(round(bobOutputs[i]), unkeyedInputs);
            }
            bobFitness = getFitness(bobMSE);
            for (int i = 0; i < genePoolSize; i++) {
                eveMSE[i] = getMSE(round(eveOutputs[i]), unkeyedInputs);
            }
            eveFitness = getFitness(eveMSE);
            float[][] testData = generateTrainingData(trainingDataSize, blockLength);
            float[][] testKeys = generateTrainingData(trainingDataSize, keylength);
            float[][] testCipherText = alice.genePool[(int)aliceFitness.get(0)].evaluate(appendKey(testData, testKeys));
            float bobTestError = getMSE(bob.genePool[(int)bobFitness.get(0)].evaluate(testData), testData);
            float eveTestError = getMSE(eve.genePool[(int)eveFitness.get(0)].evaluate(appendKey(testCipherText, testKeys)), testData);
            System.out.println(loop + ": Alice: " + " Bob: " + (getMSE(bob.bestOutput, unkeyedInputs)) * 16 + " " + " Eve: " + (getMSE(eve.bestOutput, unkeyedInputs)) * 16 + " " + " Percent difference (should be zero): " + getMSE(bob.bestOutput, unkeyedInputs) / getMSE(eve.bestOutput, unkeyedInputs) + " Bob Test error " + bobTestError*16 + " Eve Test error " + eveTestError*16);
            if (getMSE(eve.bestOutput, unkeyedInputs) * blockLength > blockLength / 2 - 1 && getMSE(bob.bestOutput, unkeyedInputs) * blockLength < 1) {
                Network[] cryptoSystem = {alice.genePool[(int) aliceFitness.get(0)], bob.genePool[(int) bobFitness.get(0)]};
                return cryptoSystem;
            }
            //aliceOutputs = alice.evolve(aliceFitness, aliceMSE, mutateFactor);
            bob.setInput(appendKey(alice.bestOutput, keys));
            eve.setInput(alice.bestOutput);
            bobOutputs = bob.evolve(bobFitness, bobMSE);
            bob.setInput(appendKey(alice.bestOutput, keys));
            eveOutputs = eve.evolve(eveFitness, eveMSE);
            eve.setInput(alice.bestOutput);
            aliceOutputs = alice.evolve(aliceFitness, aliceMSE);
            //mutateFactor-= 0.0001;
            /*
            if (loop%100 == 0) {
                String aliceFileName = "D:\\AI\\Alice\\BobEncryptMe" + loop + ".bmp";
                String bobFileName = "D:\\AI\\Bob\\BobEncryptMe" + loop + ".bmp";
                String eveFileName = "D:\\AI\\Eve\\BobEncryptMe" + loop + ".bmp";
                float[][] aliceImage = round(alice.genePool[alice.bestIndex].evaluate(appendKey(image, keys)));
                float[][] bobImage = round(bob.genePool[bob.bestIndex].evaluate(appendKey(aliceImage, keys)));
                float[][] eveImage = round(eve.genePool[eve.bestIndex].evaluate(appendKey(aliceImage, keys)));
                Main.floatArrayToFile(aliceImage, aliceFileName);
                Main.floatArrayToFile(eveImage, eveFileName);
                Main.floatArrayToFile(bobImage, bobFileName);
            }
            *
            * There might n0t be enough memory in the world to run this if statement.
            */
            loop++;
            }

    }


    public static float[][] generateTrainingData(int x, int y) {
        float[][] trainingData = new float[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                trainingData[i][j] = getRandomBit();
            }
        }
        return trainingData;
    }
    public static int getRandomBit() {
        float random = (float) Math.random()*2 - 1;
        if (random < 0) {
            return 0;
        }
        return 1;
    }


    public static float getMSE(float[][] estimate, float[][] optimal) {
        float n = 0;
        float sigma = 0;
        for (int i = 0; i < estimate.length; i++) {
            for (int j = 0; j < estimate[i].length; j++) {
                float error = (estimate[i][j] - optimal[i][j])*(estimate[i][j] - optimal[i][j]);
                sigma+=error;
                n++;
            }
        }

        return sigma / n;
    }

    public static List getFitness(float[] error){
        List errorList = new ArrayList();
        List fitness = new ArrayList();
        for (int i = 0; i < error.length; i++) {
            errorList.add(i, error[i]);
        }
        for (int i = 0; i < errorList.size(); i++) {
            fitness.add(i, errorList.indexOf(Collections.min(errorList)));
            errorList.remove((int)fitness.get(i));
            errorList.add((int)fitness.get(i), Float.POSITIVE_INFINITY);
        }
        return fitness;
    }

    public static float[][] appendKey(float[][] block, float[][] key) {
        float[][] product = new float[block.length][block.length + key.length];
        for (int i = 0; i < product.length; i++) {
            product[i] = block[i];
            for (int j = block[i].length; j < product[i].length; j++) {
                product[i][j] = key[i][j];
            }
        }
        return product;
    }
    public static float[][] round(float[][] input) {
        float[][] output = new float[input.length][input[0].length];
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[i].length; j++) {
                output[i][j] = Math.round(input[i][j]);
            }
        }
        return output;
    }

    public static float[][] encrypt(float[][] input, float[] key) {
        float[][] twoDKey = new float[input.length][key.length];
        for(int i = 0; i < twoDKey.length; i++) {
            twoDKey[i] = key;
        }
        return encryption.evaluate(appendKey(input, twoDKey));
    }
    public static float[][] decrypt(float[][] input, float[] key) {
        float[][] twoDKey = new float[input.length][key.length];
        for(int i = 0; i < twoDKey.length; i++) {
            twoDKey[i] = key;
        }
        return decryption.evaluate(appendKey(input, twoDKey));
    }

}
