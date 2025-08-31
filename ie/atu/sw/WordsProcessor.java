package ie.atu.sw;

import java.io.BufferedWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * WordsProcessor class responsible for the word processing.
 * Have methods which are used for the identified matches as direct, numeric,
 * punctuation, long numbers
 */
public class WordsProcessor {
    private static final Pattern IS_NUMBER = Pattern.compile("\\d+");
    private static final Pattern BY_PUNCTUATION = Pattern.compile("\\p{Punct}");
    private static final Pattern BY_ELEMENT = Pattern.compile("");
    private static final Pattern WORDS_AND_PUNCTUATION = Pattern.compile("(\\w+|[-\\p{Punct}])");

    /**
     * Check if the current word needs to be capitalised.
     * When the previous or next character "." or empty line or initials
     *
     * @param previousElement previous element
     * @param previousEmpty   is the previous element empty string
     * @param str             current string to check its length
     * @param nextElement     used to check is
     * @return true if the condition matched
     */
    public static boolean isCapitalized(String previousElement, boolean previousEmpty, String str, String nextElement) {
        return previousElement.equals(".") || previousEmpty || str.length() == 1 && nextElement.equals(".");
    }

    /**
     * Check is the might be numeric used for a future process long numbers
     *
     * @param word current word
     * @return true if an element can be converted to the number
     */
    public static boolean checkNumeric(String word) {
        //check if string might be the number
        Matcher pattern = IS_NUMBER.matcher(word);
        return pattern.matches();
    }

    /**
     * Checking if the number bigger than 9 via traversing over each item
     *
     * @param previousNumeric the flag that identifies if a previous element was number
     * @param currentElement  element to check
     * @return true if yes
     */
    public static boolean checkLongNumber(boolean previousNumeric, String currentElement) {
        //check if the current element is number over 10 via comparing adjacent elements
        boolean isNumber = checkNumeric(currentElement);
        return previousNumeric && isNumber;
    }

    /**
     * Used to check if the current element has punctuation in it
     *
     * @param isLongNumber   if element long number used for dates ranges
     * @param nextElement    next element to check
     * @param currentElement element to compare with
     * @return true if got match
     */
    public static boolean isPunctuation(boolean isLongNumber, String nextElement, String currentElement) {
        Matcher matcher = BY_PUNCTUATION.matcher(nextElement);
        boolean matchedByPunctuation = matcher.find();

        return isPunctuationMatch(isLongNumber, nextElement, currentElement, matchedByPunctuation);
    }


    /**
     * Method used to identify punctuation match used in both
     * encoding and decoding
     *
     * @param isLongNumber         long number flag
     * @param nextElement          to compare
     * @param currentElement       current element
     * @param matchedByPunctuation true or false
     * @return true if got match
     */
    private static boolean isPunctuationMatch(boolean isLongNumber, String nextElement, String currentElement, boolean matchedByPunctuation) {
        return isLongNumber
                || (!nextElement.equals("[???]") && matchedByPunctuation && !nextElement.equals("("))
                || currentElement.equals("(")
                || currentElement.equals("-")
                || checkNumeric(currentElement) && checkNumeric(nextElement)
                || (currentElement.equals(":") && checkNumeric(nextElement))
                || currentElement.equals("'");
    }


    /**
     * Check if the current element has an exact match in the words-list
     *
     * @param word            current word
     * @param wordsList       list of elements to search in
     * @param indexToEncode   index of the array to looking for elements
     * @param mode            current mode
     * @param wordsListLength length of words list
     * @param out             output file
     * @return if found exact match return true
     */
    public static boolean isExactMatch(String word,
                                       Object[][] wordsList,
                                       int indexToEncode,
                                       ArrayMode mode,
                                       int wordsListLength,
                                       BufferedWriter out) {
        // Searches for a word in a specific list and processes it if found.
        boolean needToFind = true;
        int index = 0;

        while (needToFind && index < wordsListLength) {
            if (word.equals(wordsList[indexToEncode][index])) {
                //add found word to the resulting file
                FileReader.stringBuilder(wordsList, mode, index, out);
                needToFind = false;
            }
            index++;
        }

        return needToFind; // Indicates whether the word was found and processed
    }


    /**
     * Take the long number, separate it on the parts and encode
     * return true when encoding attempt fault
     *
     * @param partition       part of the word which considered to be the number
     * @param wordsList       array of the words where we need to find a number
     * @param indexToEncode   index of the row in which we take the element to compare
     * @param mode            mode encode or decode
     * @param wordsListLength length of the word list to iterate over
     * @param out             file for the output
     * @return boolean value that reflects if you still need to find value
     */
    public static boolean isNumbersdecoded(String partition,
                                           Object[][] wordsList,
                                           int indexToEncode,
                                           ArrayMode mode,
                                           int wordsListLength,
                                           BufferedWriter out) {
        String[] numbers = BY_ELEMENT.split(partition);
        boolean needToFind = true;
        for (String number : numbers) {
            needToFind = isExactMatch(number, wordsList, indexToEncode,
                    mode, wordsListLength, out);
        }
        return needToFind;
    }


    /**
     * get elements with punctuation
     * and separate them by word and punctuation
     *
     * @param element string which might have punctuation init
     * @return array of strings or Zero length array
     */
    private static String[] getElementsWithPunctuation(String element) {
        Matcher matcher = WORDS_AND_PUNCTUATION.matcher(element);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        String[] resultArray = new String[count];

        matcher.reset();
        int matchesCount = 0;
        while (matcher.find()) {
            resultArray[matchesCount++] = matcher.group();
        }

        return resultArray;
    }


    /**
     * Process words with punctuation and return false
     * if no need to make next checks
     * @param word         word to process
     * @param wordsList    words list for future match search
     * @param suffixesList suffixes list
     * @param out          file for the output
     * @param startIndex   the index from which beginning searches
     * @param mode         encode or decode
     * @return false if no need to look for the elements in this list
     */
    public static boolean processWordsWithPunctuation(String word,
                                                      Object[][] wordsList,
                                                      Object[][] suffixesList,
                                                      BufferedWriter out,
                                                      int startIndex, ArrayMode mode) {
        boolean isDecode = mode == ArrayMode.DECODE;
        int indexToEncode = CipherProcessor.getArrayIndex(mode, isDecode);
        //build the result array of elements which have punctuation inside
        String[] withPunctuation = getElementsWithPunctuation(word);
        int wordsListLength = wordsList[indexToEncode].length;

        if (withPunctuation.length == 0) {
            FileReader.stringBuilder(wordsList, mode, startIndex, out);
            return false;
        }

        for (String partition : withPunctuation) {
            int index = 0;
            boolean needToFind = true;
            while (needToFind && index < wordsListLength) {
                //checking if the current part is a number
                if (checkNumeric(partition)) {
                    needToFind = isNumbersdecoded(partition, wordsList, indexToEncode,
                            mode, wordsListLength, out);
                }
                //checking if current element in the map
                else if (partition.equals(wordsList[indexToEncode][index])) {
                    FileReader.stringBuilder(wordsList, mode, index, out);
                    needToFind = false;
                }
                index++;
            }

            if (needToFind) {
                SuffixProcessor.processSuffixes(partition, wordsList, suffixesList, indexToEncode, mode, wordsListLength,
                        out);
            }
        }

        return false;
    }
}
