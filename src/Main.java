import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.BitSet;
import java.util.Scanner;

/**
 *
 */
public class Main {
    public static int blockLength = 8;
    public static int keylength = 8;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Training:");
        CryptoSystem cryptoSystem = new CryptoSystem(blockLength, keylength);
        while(true) {
            System.out.print("Type the path of the file you'd like to encrypt \n>");
            String pathIn = scanner.next();
            System.out.print("Type your 8 bit binary key now.\n>");
            byte key = (byte) Integer.parseInt(scanner.next(), 2);
            float[][] filePlaintext = fileToFloatArray(pathIn);
            BitSet keyBits = BitSet.valueOf(new byte[]{key});
            float[] floatKey = new float[keyBits.length()];
            for (int i = 0; i < keyBits.length(); i++) {
                if (keyBits.get(i)) {
                    floatKey[i] = 1;
                } else {
                    floatKey[i] = 0;
                }
            }
            float[][] cipherText = CryptoSystem.round(cryptoSystem.encrypt(filePlaintext, floatKey));
            System.out.print("Where would you like the output file to wind up?\n>");
            String pathOut = scanner.next();
            floatArrayToFile(cipherText, pathOut);


            System.out.print("Type the path of the file you'd like to decrypt \n>");
            pathIn = scanner.next();
            float[][] fileCipherText = fileToFloatArray(pathIn);
            float[][] filePlainText = CryptoSystem.round(cryptoSystem.decrypt(fileCipherText, floatKey));
            System.out.print("Where would you like the output file to wind up?\n>");
            pathOut = scanner.next();
            floatArrayToFile(filePlainText, pathOut);
        }
    }
    public static float[][] fileToFloatArray(String pathIn) {

        byte[] fileContents = new byte[]{};
        try {
            fileContents = Files.readAllBytes(new File(pathIn).toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        BitSet[] bits = new BitSet[fileContents.length];
        float[][] plaintext = new float[fileContents.length][blockLength];
        for (int i = 0; i < fileContents.length; i++) {
            bits[i] = BitSet.valueOf(new byte[]{fileContents[i]});
            for (int j = 0; j < 8; j++) {
                if (bits[i].get(j)) {
                    plaintext[i][j] = 1;
                } else {
                    plaintext[i][j] = 0;
                }
            }
        }
        return plaintext;

    }
    public static void floatArrayToFile (float[][] cipherText, String pathOut) {
        BitSet encryptedBits = new BitSet();
        for (int i = 0; i < cipherText.length; i++) {
            for (int j = 0; j < cipherText[i].length; j++) {
                if (cipherText[i][j] == 1) {
                    encryptedBits.set(i*8 + j, true);
                }
                else {
                    encryptedBits.set(i*8 + j, false);
                }
            }
        }
        byte[] encryptedFileContents = encryptedBits.toByteArray();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(pathOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(encryptedFileContents);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
