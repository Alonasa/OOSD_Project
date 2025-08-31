package ie.atu.sw;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * The FileReader class is responsible for processing file operations:
 * encoding, decoding, or parsing file data.
 * The class provides methods for reading input files,
 * processing content using specific logic,
 * and writing the processed data to output files.
 */
public class FileReader {
    //Regex patterns
    private static final Pattern BY_WORD = Pattern.compile("\\s+");
    private static final Pattern BY_COMMA = Pattern.compile(",");
    //Modes
    private static final ArrayMode WORDS_MODE = ArrayMode.WORDS;
    private static final ArrayMode SUFFIXES_MODE = ArrayMode.SUFFIXES;
    private static final ArrayMode ENCODE_MODE = ArrayMode.ENCODE;

    /**
     * Processes a file by reading its contents, applying encoding
     * or decoding operations, and writing the results to
     * a specified output file
     *
     * @param source the path to the source file
     * @param output the path to the output file to store the processed content
     * @param array a multidimensional array containing data required for encoding or decoding
     * @param mode the mode, either encoding or decoding, defined by the ArrayMode enum
     */
    public static void processFile(String source, String output, Object[][] array, ArrayMode mode) {
        try {
            BufferedReader br = getBufferedReader(source);
            BufferedWriter out = new BufferedWriter(new FileWriter(output, true), 8192);
            int amountOfLines = getAmountOfLines(source);
            int linesProcessed = 0;
            boolean isEncodeMode = mode == ENCODE_MODE;

            String line = null;

            boolean previousLineEmpty = true;

            while ((line = br.readLine()) != null) {
                System.out.print(ConsoleColour.YELLOW);//Change the colour of the console text
                ProgressBar.printProgress(linesProcessed + 1, amountOfLines);
                String lowerCased = line.toLowerCase(Locale.ROOT);
                //Generate a mini array of words from a line element
                String[] words = isEncodeMode ? BY_WORD.split(lowerCased) : BY_COMMA.split(line);
                int wordsLength = words.length;

                Object[][] wordsList = ArraysProcessor.getArrayPartition(array, WORDS_MODE);
                Object[][] suffixesList = ArraysProcessor.getArrayPartition(array, SUFFIXES_MODE);



                //traversing through words array
                for (int wordIndex = 0; wordIndex < wordsLength; wordIndex++) {
                    if (isEncodeMode) {
                        CipherProcessor.encode(words, wordsList, suffixesList, wordIndex, mode, out);
                    } else {
                        CipherProcessor.decode(words, array, wordIndex, mode, out, previousLineEmpty);
                        previousLineEmpty = line.length() == 2;
                    }
                }
                out.write("\n");
                linesProcessed++;
            }
            out.flush();
            out.close();
            br.close();
            System.out.println("Successfully decoded file");

        } catch (IOException e) {
            UtilMethods.printErrorMessage("I/O Error on decoding: ", e);
        }
    }


    /**
     * @param array the array of elements to process
     * @param mode current mode
     * @param index index of the current element
     * @param out the FileWriter, which attach string and comma after each index
     */
    public static void stringBuilder(Object[][] array, ArrayMode mode, int index, BufferedWriter out) {
        //Writes an element from the array to the output file with a comma separator
        boolean isDecodeMode = mode == ArrayMode.ENCODE;
        int indexToDecode = CipherProcessor.getArrayIndex(mode, isDecodeMode);
        try {
            String str = String.valueOf(array[indexToDecode][index]);
            out.write(str);
            out.write(",");
        } catch (IOException e) {
            UtilMethods.printErrorMessage("Error writing to output file: ", e);
        }
    }


    /**
     * Optimised File I/O Operations by adding extended buffer reader size
     * @param source - address of the source to process
     * @return BufferedReader or null
     */
    public static BufferedReader getBufferedReader(String source) {
        // Creates a BufferedReader for the specified source file
        BufferedReader result = null;

        try {
            result = new BufferedReader(new InputStreamReader(
                    new FileInputStream(source), StandardCharsets.UTF_8), 8192);
        } catch (FileNotFoundException e) {
            String errorMessage = UtilMethods.buildString("File not found: ", source);
            UtilMethods.printErrorMessage(errorMessage, e);
        }
        return result;
    }

    /**
     * Get a number of lines in the processing file.
     * Used for a progress meter
     * @param source the file location
     * @return number of lines
     */
    public static int getAmountOfLines(String source) {
        int amountOfLines = 0;
        try (BufferedReader br = getBufferedReader(source)) {
            while (br.readLine() != null) {
                amountOfLines++;
            }
            return amountOfLines;
        } catch (IOException e) {
            UtilMethods.printErrorMessage("Error: ", e);
        }
        return amountOfLines;
    }

}