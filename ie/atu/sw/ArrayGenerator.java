package ie.atu.sw;

import java.io.BufferedReader;
import java.util.Arrays;

public class ArrayGenerator {
    private static final int DECODED = 0;
    private static final int ENCODED = 1;

    public Object[][] getArray(String source) throws Exception {
        BufferedReader br = FileReader.getBufferedReader(source);
        Object[] sizes = getArrayLength(getAmountOfElements(source));
        Object[][] array = createArray(br, sizes);
        return array;
    }

    public void printArray(String[][] array) {
        System.out.println(Arrays.deepToString(array));
    }


    private Object[] getArrayLength(int amountOfElements) throws Exception {
        int arrayLength = 2;

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


    private Object[][] createArray(BufferedReader br, Object[] sizes) throws Exception {
        int amountOfElements = (Integer) sizes[0];
        int arrayLength =  (Integer) sizes[1];
        Object[][] array = new Object[arrayLength][amountOfElements];

        String line;
        int i = 0;

        while ((line = br.readLine()) != null) {
            String[] newLine = line.split(",");
            array[DECODED][i] = newLine[DECODED];
            array[ENCODED][i] = Integer.parseInt(newLine[ENCODED]);
            i++;
        }

        return array;
    }
}
