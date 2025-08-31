package ie.atu.sw;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * The SuffixProcessor class provides utility methods for processing
 * and handling suffix-related operations on strings.
 * This includes functionalities such as identifying suffixes,
 * splitting words by suffixes, and processing suffixes in
 * conjunction with word lists and modes.
 */
public class SuffixProcessor {
    private static final Pattern BY_SUFFIX = Pattern.compile("@@");
    private static final int ZERO_ELEMENT = 0;


    public static void buildSuffixString(String str, BufferedWriter out) throws IOException {
        boolean gotSuffix = checkForSuffix(str);
        if (gotSuffix) {
            String suffix = splitSuffix(str);
            out.write(suffix);
        } else {
            out.write(str);
        }
    }

    /**
     * Processes suffixes for a given word by matching it against a suffix
     *
     * @param word            the word to process, for future suffixes check
     * @param wordsList       a 2D array containing words
     * @param suffixesList    a 2D array containing suffixes
     * @param indexToEncode   the index to encode in wordsList
     * @param mode            the processing mode encoding or decoding
     * @param wordsListLength the total number of entries in wordsList
     * @param out             the file to save a result
     */
    public static void processSuffixes(String word,
                                       Object[][] wordsList,
                                       Object[][] suffixesList,
                                       int indexToEncode,
                                       ArrayMode mode,
                                       int wordsListLength,
                                       BufferedWriter out) {
        //Method for the processing suffixes. Start from the longest suffix match
        // If no match returns -1 and sends element 0 to the string builder

        int suffixIndex = getSuffixIndex(suffixesList, word);
        if (suffixIndex > ZERO_ELEMENT) {
            // If matched, separate the suffix from @@, splits it with the rest of the word
            String bareSuffix = (suffixesList[ZERO_ELEMENT][suffixIndex]).toString();
            String cleanSuffix = splitSuffix(bareSuffix);
            String[] wordToCheck = splitWord(word, cleanSuffix);
            if (wordToCheck.length > ZERO_ELEMENT) {
                //Added prefix for the future recursive call
                String prefix = wordToCheck[ZERO_ELEMENT];
                // Then check if the prefix has a match
                boolean needToFind = WordsProcessor.isExactMatch(prefix,
                        wordsList, indexToEncode,
                        mode, wordsListLength, out);

                if (needToFind) {
                    // recursively process the prefix till get the full match
                    processSuffixes(prefix, wordsList, suffixesList, indexToEncode, mode, wordsListLength, out);
                }
            }

            // After processing the prefix (if needed), add the suffix
            FileReader.stringBuilder(suffixesList, mode, suffixIndex, out);
        } else {
            FileReader.stringBuilder(wordsList, mode, ZERO_ELEMENT, out);
        }
    }

    /**
     * Splits the given word into an array of substrings
     *
     * @param word   the word to be split
     * @param suffix the delimiter used to split the word
     * @return an array of substrings resulting from splitting the word
     */
    private static String[] splitWord(String word, String suffix) {
        return word.split(suffix);
    }


    /**
     * Checks if a word has a valid suffix in it
     *
     * @param word to check
     * @return true in case if it has suffix in it or false
     */
    public static boolean checkForSuffix(String word) {
        String prefix = String.valueOf(BY_SUFFIX);
        return word.startsWith(prefix);
    }

    /**
     * Split word by matched suffix and return suffix for
     * the string building
     *
     * @param word word for future splitting
     * @return current suffix
     */
    private static String splitSuffix(String word) {
        return BY_SUFFIX.split(word)[1];
    }


    /**
     * Get index of the current suffix in a suffixes-list
     *
     * @param suffixesList list of suffixes
     * @param element      current element to process
     * @return suffix index or -1 in case if it not in the list
     */
    static int getSuffixIndex(Object[][] suffixesList, String element) {
        //Iterate over all suffixes and looking for the longest match
        int suffixesLength = suffixesList[0].length;
        int bestSuffixIndex = -1;
        int bestSuffixLength = 0;

        for (int index = 0; index < suffixesLength; index++) {
            String bareSuffix = (suffixesList[0][index]).toString();
            String cleanSuffix = splitSuffix(bareSuffix);
            boolean isSuffixMatch = element.endsWith(cleanSuffix);
            int suffixLength = cleanSuffix.length();

            if (isSuffixMatch && suffixLength > bestSuffixLength) {
                bestSuffixIndex = index;
                bestSuffixLength = suffixLength;
            }
        }

        return bestSuffixIndex;
    }
}
