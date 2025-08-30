package ie.atu.sw;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;

public class ArraysProcessor {
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
        }

        return array;
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
        } catch (IOException e) {
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
        } catch (IOException e) {
            LoggerUtil.printErrorMessage("Error: ", e);
        }
        return amountOfLines;
    }


    public static Object[][] createArray(BufferedReader br, int[] sizes) {
        int amountOfElements = sizes[0];
        int arrayLength = sizes[1];
        Object[][] elementsToEncode = new Object[arrayLength][amountOfElements];
        String line;
        int i = 0;
        try (br) {
            while ((line = br.readLine()) != null) {
                String[] newLine = line.split(",");
                elementsToEncode[DECODED_INDEX][i] = newLine[DECODED_INDEX];
                elementsToEncode[ENCODED_INDEX][i] = newLine[ENCODED_INDEX];
                i++;
            }
        } catch (IOException e) {
            LoggerUtil.printErrorMessage("I/O Error");
        }
        return elementsToEncode;
    }


    public static Object[][] getArrayPartition(Object[][] initialArray, ArrayMode mode) {
        // Count the number of elements for the future array
        int initialArrayLength = initialArray.length;
        int rowCount = countElementsForSeparation(initialArray[0], mode);

        // Create a new array with the filtered size
        Object[][] filteredArray = new Object[initialArrayLength][rowCount];

        // Populate the new array according to mode
        int index = 0;
        int arrayLength = initialArray[0].length;
        for (int i = 0; i < arrayLength; i++) {
            if (initialArray[0][i] != null) {
                String value = initialArray[0][i].toString();
                boolean isSuffix = SuffixProcessor.checkForSuffix(value);

                // Check if this element matches the mode criteria
                boolean isSuffixes = (mode == FileReader.SUFFIXES_MODE && isSuffix);
                boolean isWords = (mode != FileReader.SUFFIXES_MODE && !isSuffix);
                if (isSuffixes || isWords) {
                    // Copy both decoded and encoded values to maintain a relationship
                    filteredArray[DECODED_INDEX][index] = initialArray[DECODED_INDEX][i];
                    filteredArray[ENCODED_INDEX][index] = initialArray[ENCODED_INDEX][i];
                    index++;
                }
            }
        }
        return filteredArray;
    }


    public static int countElementsForSeparation(Object[] elements, ArrayMode mode) {
        //method used to count amount elements for the future filtration in arrays
        int counter = 0;
        for (Object row : elements) {
            String value = row.toString();
            boolean isSuffix = SuffixProcessor.checkForSuffix(value);
            boolean isSuffixMode = isSuffixMode(mode, isSuffix);
            if (isSuffixMode) {
                counter++;
            }
        }
        return counter;
    }


    private static boolean isSuffixMode(ArrayMode mode, boolean isSuffix) {
        return (mode == FileReader.SUFFIXES_MODE) == isSuffix;
    }
}
