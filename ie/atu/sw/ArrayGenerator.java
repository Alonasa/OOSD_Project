package ie.atu.sw;

import java.io.BufferedReader;
import java.util.Arrays;

public class ArrayGenerator {
    int ENCODED = 0;
    int DECODED = 1;

    public String[][] getArray(String source) throws Exception {
        BufferedReader br = FileReader.getBufferedReader(source);
        Object[] sizes = getArrayLength(getAmountOfElements(source));
        String[][] array = createArray(br, sizes);
        return array;
    }

    public void printArray(String[][] array) {
        System.out.println(Arrays.deepToString(array));
    }


    private Object[] getArrayLength(int amountOfElements) throws Exception {
        short arrayLength = 2;

        Object[] sizes = new Object[arrayLength];

        sizes[0] = amountOfElements;
        sizes[1] = arrayLength;
        return sizes;
    }


    public int getAmountOfElements(String source) throws Exception {
        BufferedReader br = FileReader.getBufferedReader(source);
        int amountOfElements = 0;
        while (br.readLine() != null) amountOfElements++;
        return amountOfElements;
    }

    public int getAmountOfLines(String source) throws Exception {
        BufferedReader br = FileReader.getBufferedReader(source);
        int amountOfLines = 0;
        while (br.readLine() != null) amountOfLines++;
        return amountOfLines;
    }


    private String[][] createArray(BufferedReader br, Object[] sizes) throws Exception {
        int amountOfElements = (Integer) sizes[0];
        short arrayLength = (Short) sizes[1];
        String[][] array = new String[arrayLength][amountOfElements];

        String line;
        int i = 0;

        while ((line = br.readLine()) != null) {
            String[] newLine = line.split(",");
            array[ENCODED][i] = newLine[ENCODED];
            array[DECODED][i] = newLine[DECODED];
            i++;
        }

        return array;
    }
}
