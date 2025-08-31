package ie.atu.sw;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * The class which is aimed to work with arrays
 * Can return an array, create an array, filtered array part, length,
 * number of elements for future array creation, count elements for filtration,
 * check the array-mode
 */
public class ArraysProcessor {
    private static final int DECODED_INDEX = 0;
    private static final int ENCODED_INDEX = 1;
    private static final ArrayMode SUFFIXES_MODE = ArrayMode.SUFFIXES;


    /**
     * @param source file location
     * @return 2D array
     */
    public static Object[][] getArray(String source) {
        Object[][] array = null;
        try (BufferedReader br = FileReader.getBufferedReader(source)) {
            int amountOfElements = getAmountOfElements(source);
            int[] sizes = getArrayLength(amountOfElements);
            array = createArray(br, sizes);
        } catch (IOException e) {
            UtilMethods.printErrorMessage("I/O Error Occur");
        }

        return array;
    }


    /**
     * Take the file location and build 2D array based on it's content
     *
     * @param br Buffer reader
     * @param sizes sizes of elements
     * @return 2D array
     */
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
            UtilMethods.printErrorMessage("I/O Error");
        }
        return elementsToEncode;
    }


    /**
     * Method used for the filtering array for the suffixes or words
     * @param initialArray big array
     * @param mode word or suffix
     * @return 2D array of required partitions
     */
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
                boolean isSuffixes = (mode == SUFFIXES_MODE && isSuffix);
                boolean isWords = (mode != SUFFIXES_MODE && !isSuffix);
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


    /**
     * Generates an array of future array sizes
     * @param amountOfElements number of elements in the future array
     * @return array with sizes of the future array
     */
    private static int[] getArrayLength(int amountOfElements) {
        int arrayLength = 2;
        int[] sizes = new int[arrayLength];
        sizes[0] = amountOfElements;
        sizes[1] = arrayLength;

        return sizes;
    }


    /**
     * Count the number of elements for in a future array
     *
     * @param source file location
     * @return the size of the future array
     */
    private static int getAmountOfElements(String source) {
        int amountOfElements = 0;
        try (BufferedReader br = FileReader.getBufferedReader(source)) {
            while (br.readLine() != null) {
                amountOfElements++;
            }
        } catch (IOException e) {
            UtilMethods.printErrorMessage("Error occurred: ", e);
        }

        return amountOfElements;
    }

    /**
     * The method used for the counting array size for suffix or word mode.
     * The goal is to optimise the number of loops in comparing
     *
     * @param elements array of elements to search for matches
     * @param mode     current mode Words or Suffixes
     * @return array size
     */
    private static int countElementsForSeparation(Object[] elements, ArrayMode mode) {
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


    /**
     * Check if the current mode is suffix-mode
     * @param mode words or suffix
     * @param isSuffix flag identify current mode
     * @return true or false
     */
    private static boolean isSuffixMode(ArrayMode mode, boolean isSuffix) {
        return (mode == SUFFIXES_MODE) == isSuffix;
    }
}
