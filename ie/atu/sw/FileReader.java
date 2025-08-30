package ie.atu.sw;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.regex.Pattern;

public class FileReader {
    //Regex patterns
    private static final Pattern BY_WORD = Pattern.compile("\\s+");
    private static final Pattern BY_COMMA = Pattern.compile(",");
    //Modes
    private static final ArrayMode ENCODE_MODE = ArrayMode.ENCODE;
    private static final ArrayMode DECODE_MODE = ArrayMode.DECODE;
    public static final ArrayMode SUFFIXES_MODE = ArrayMode.SUFFIXES;

    public static void processFile(String source, String output, Object[][] array, ArrayMode mode) {
        try {
            BufferedReader br = getBufferedReader(source);
            BufferedWriter out = new BufferedWriter(new FileWriter(output, true), 8192);
            ArraysProcessor ag = new ArraysProcessor();
            int amountOfLines = ag.getAmountOfLines(source);
            int indexToDecode = mode == DECODE_MODE ? 0 : 1;
            int indexToEncode = mode == ENCODE_MODE ? 0 : 1;
            int linesProcessed = 0;
            boolean encodeMode = mode == ENCODE_MODE;

            String line = null;

            boolean previousLineEmpty = true;

            while ((line = br.readLine()) != null) {
                System.out.print(ConsoleColour.YELLOW);//Change the colour of the console text
                ProgressBar.printProgress(linesProcessed + 1, amountOfLines);
                String lowerCased = line.toLowerCase(Locale.ROOT);
                //Generate a mini array of words from a line element
                String[] words = encodeMode ? BY_WORD.split(lowerCased) : BY_COMMA.split(line);

                int wordsLength = words.length;


                //traversing through words array
                for (int wordIndex = 0; wordIndex < wordsLength; wordIndex++) {
                    if (encodeMode) {
                        CipherProcessor.encode(array, words, wordIndex, mode, out);
                    } else {
                        CipherProcessor.decode(words, array, indexToDecode, wordIndex, out, previousLineEmpty);
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
     * Process empty characters
     * @param out the FileWriter, which attach string and comma after each index
     */
    public static void stringBuilder(int magicNumber, FileWriter out) {
        //Writes an element from the array to the output file with a comma separator
        try {
            out.write(magicNumber);
            out.write(",");
        } catch (IOException e) {
            UtilMethods.printErrorMessage("Error writing ' ' to file: ", e);
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

}