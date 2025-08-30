package ie.atu.sw;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Locale;

public class CipherProcessor {
    private static final ArrayMode ENCODE_MODE = ArrayMode.ENCODE;
    private static final int DECODED_INDEX = 0;
    private static final ArrayMode WORDS_MODE = ArrayMode.WORDS;


    public static void decode(String source, Object[][] array, ArrayMode mode) {
        String outputLocation = mode == ENCODE_MODE ? "./out.txt" : "./out-decoded.txt";
        FileReader.processFile(source, outputLocation, array, mode);
    }

    /**
     * Decode data from the provided array and save it to a file via BufferedWriter.
     * The method processes the words in decoded mode via match-by-word index in the array
     * if the match didn't find, set it to the default value which is located on index 0
     * @param words array of words from read line
     * @param array array of to process the indexes
     * @param indexToDecode index by which we get an element from the array
     * @param wordIndex index of the current word
     * @param out output file
     * @param previousLineEmpty flag used to make first words capital if there is an empty line preceded
     * @throws IOException throws IO exception if something went wrong with bufferedWriter
     */

    public static void decode(String[] words, Object[][] array, int indexToDecode, int wordIndex,
                              BufferedWriter out, boolean previousLineEmpty) throws IOException {
        int wordsLength = words.length;
        boolean nextNumeric = false;
        boolean firstEmpty = words[DECODED_INDEX].equals("0") || words[DECODED_INDEX].equals(" ");

        String str = array[indexToDecode][Integer.parseInt(words[wordIndex])].toString();

        String nextElement = array[indexToDecode][Integer.parseInt(words[(wordIndex + 1 < words.length ?
                wordIndex + 1 :
                wordIndex)])].toString();
        String previousElement = array[indexToDecode][Integer.parseInt(words[(wordIndex - 1 >= 0 ?
                wordIndex - 1 :
                wordIndex)])].toString();

        if (!(wordsLength == 1 && firstEmpty)) {
            if (WordsProcessor.isCapitalized(previousElement, previousLineEmpty, str, nextElement)) {
                String firstLetter = str.substring(0, 1).toUpperCase(Locale.ROOT);
                String restOfTheWord = str.substring(1);
                String capitalized = LoggerUtil.buildWord(firstLetter, restOfTheWord);
                out.write(capitalized);
            } else {
                SuffixProcessor.buildSuffixString(str, out);
                nextNumeric = WordsProcessor.checkNumeric(nextElement);
            }
        }


        boolean isLongNumber = WordsProcessor.checkLongNumber(nextNumeric, str);

        if (WordsProcessor.isPunctuation(isLongNumber, nextElement, str)) {
            out.write("");
        } else {
            out.write(" ");
        }
    }

    /**
     * Encodes data from the provided array and save it to a file via BufferedWriter.
     * The method processes the words based on the given mode and performs encoding operations
     * while handling exact matches and words with punctuation.
     *
     * @param array 2D array containing objects for encoding
     * @param words An array of strings representing the words from a line
     * @param wordIndex The index of the current word
     * @param mode The mode in which the encoding should operate
     * @param out A BufferedWriter to write the results
     */
    public static void encode(Object[][] array, String[] words, int wordIndex, ArrayMode mode, BufferedWriter out) {
        int index = 0;
        Object[][] wordsList = ArraysProcessor.getArrayPartition(array, WORDS_MODE);
        Object[][] suffixesList = ArraysProcessor.getArrayPartition(array, FileReader.SUFFIXES_MODE);

        int indexToEncode = 0;

        int wordsListLength = wordsList[indexToEncode].length;

        boolean needToFind = true;
        while (needToFind) {
            //send current to check if it exists in a mapping file
            //if yes than we process to the next word otherwise going for deeper search
            if (words[wordIndex].isEmpty()) {
                needToFind = false;
                FileReader.stringBuilder(wordsList, mode, 0, out);
            } else {
                // Try to find an exact match first
                needToFind = WordsProcessor.isExactMatch(words[wordIndex], wordsList, indexToEncode,
                        mode, wordsListLength, out);

                // If no exact match found, process words with punctuation
                if (needToFind) {
                    needToFind = WordsProcessor.processWordsWithPunctuation(words[wordIndex], wordsList,
                            suffixesList, out, index, mode);
                }

            }
        }
    }

    /**
     * Get an index of an array to work with
     * @param mode current mode
     * @param isDecode is decoded mode now
     * @return integer index of an array with which we will work
     */
    public static int getArrayIndex(ArrayMode mode, boolean isDecode) {
        return isModeMatch(mode, isDecode) ? 0 : 1;
    }

    /**
     * Check if any of default modes selected
     * @param mode selected mode
     * @param isDecode check is decode mode now
     * @return if mode match return true
     */
    private static boolean isModeMatch(ArrayMode mode, boolean isDecode) {
        return (isDecode && mode == ArrayMode.DECODE) || (!isDecode && mode == ArrayMode.ENCODE);
    }

}
