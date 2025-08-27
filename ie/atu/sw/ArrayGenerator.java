package ie.atu.sw;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;

public class ArrayGenerator {
    private static final int DECODED_INDEX = 0;
    private static final int ENCODED_INDEX = 1;


    public Object[][] getArray(String source) {
        Object[][] array = null;
        try (BufferedReader br = FileReader.getBufferedReader(source)) {
            int amountOfElements = getAmountOfElements(source);
            int[] sizes = getArrayLength(amountOfElements);
            array = createArray(br, sizes);
            return array;
        } catch (IOException e) {
            LoggerUtil.printErrorMessage("I/O Error Occur");
        } catch (Exception e) {
            LoggerUtil.printErrorMessage("Error occurred: %s", e);
        }
        return array;
    }


    public void printArray(Object[][] array) {
        String arrayToPrint = Arrays.deepToString(array);
        System.out.println(arrayToPrint);
    }




    private static int[] getArrayLength(int amountOfElements) {
        int arrayLength = 2;
        int[] sizes = new int[arrayLength];
        sizes[0] = amountOfElements;
        sizes[1] = arrayLength;

        return sizes;
    }


    public static int getAmountOfElements(String source) {
        int amountOfElements = 0;
        try (BufferedReader br = FileReader.getBufferedReader(source)) {
            while (br.readLine() != null) {
                amountOfElements++;
            }
            return amountOfElements;
        } catch (Exception e) {
            LoggerUtil.printErrorMessage("Error occurred: ", e);
        }
        return amountOfElements;
    }

    public int getAmountOfLines(String source) {
        int amountOfLines = 0;
        try (BufferedReader br = FileReader.getBufferedReader(source)) {
            while (br.readLine() != null) {
                amountOfLines++;
            }
            return amountOfLines;
        } catch (Exception e) {
            LoggerUtil.printErrorMessage("Error: ", e);
        }
        return amountOfLines;
    }


    private Object[][] createArray(BufferedReader br, int[] sizes) throws Exception {
        int amountOfElements = sizes[0];
        int arrayLength = sizes[1];
        Object[][] elementsToEncode = new Object[arrayLength][amountOfElements];

        String line;
        int i = 0;

        while ((line = br.readLine()) != null) {
            String[] newLine = line.split(",");
            elementsToEncode[DECODED_INDEX][i] = newLine[DECODED_INDEX];
            elementsToEncode[ENCODED_INDEX][i] = newLine[ENCODED_INDEX];
            i++;
        }
        return elementsToEncode;
    }
}
