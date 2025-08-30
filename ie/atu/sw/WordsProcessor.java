package ie.atu.sw;

import java.io.FileWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordsProcessor {
    private static final Pattern IS_NUMBER = Pattern.compile("\\d+");
    private static final Pattern BY_PUNCTUATION = Pattern.compile("\\p{Punct}");
    private static final Pattern BY_ELEMENT = Pattern.compile("");
    private static final Pattern WORDS_AND_PUNCTUATION = Pattern.compile("(\\w+|[-\\p{Punct}])");


    public static boolean isCapitalized(String previousElement, boolean previousEmpty, String str, String nextElement) {
        return previousElement.equals(".") || previousEmpty || str.length() == 1 && nextElement.equals(".");
    }

    public static boolean checkNumeric(String word) {
        //check if string might be the number
        Matcher pattern = IS_NUMBER.matcher(word);
        return pattern.matches();
    }


    public static boolean checkLongNumber(boolean previousNumeric, String currentElement) {
        //check if the current element is number over 10 via comparing adjacent elements
        boolean isNumber = checkNumeric(currentElement);
        return previousNumeric && isNumber;
    }


    public static boolean isPunctuation(boolean isLongNumber, String nextElement, String currentElement) {
        Matcher matcher = BY_PUNCTUATION.matcher(nextElement);
        boolean matchedByPunctuation = matcher.find();

        return isPunctuationMatch(isLongNumber, nextElement, currentElement, matchedByPunctuation);
    }


    private static boolean isPunctuationMatch(boolean isLongNumber, String nextElement, String currentElement, boolean matchedByPunctuation) {
        return isLongNumber
                || (!nextElement.equals("[???]") && matchedByPunctuation && !nextElement.equals("("))
                || currentElement.equals("(")
                || currentElement.equals("-")
                || (currentElement.equals(":") && checkNumeric(nextElement))
                || currentElement.equals("'");
    }

    public static boolean isExactMatch(String word,
                                       Object[][] wordsList,
                                       int indexToEncode,
                                       ArrayMode mode,
                                       int wordsListLength,
                                       FileWriter out) {
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
     * @param partition part of the word which considered to be the number
     * @param wordsList array of the words where we need to find a number
     * @param indexToEncode index of the row in which we take the element to compare
     * @param mode mode encode or decode
     * @param wordsListLength length of the word list to iterate over
     * @param out file in which the program will write the index of the found number
     * @return boolean value that reflects if you still need to find value
     */
    public static boolean isNumbersdecoded(String partition,
                                            Object[][] wordsList,
                                            int indexToEncode,
                                            ArrayMode mode,
                                            int wordsListLength,
                                            FileWriter out) {
        String[] numbers = BY_ELEMENT.split(partition);
        boolean needToFind = true;
        for (String number : numbers) {
            needToFind = isExactMatch(number, wordsList, indexToEncode,
                    mode, wordsListLength, out);
        }
        return needToFind;
    }


    /**
     * @param element
     * @return
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
     * @param word word to process
     * @param wordsList words list for future match search
     * @param suffixesList suffixes list
     * @param out file for the output
     * @param startIndex
     * @param mode encode or decode
     * @return false if no need to look for the elements in this list
     */
    public static boolean processWordsWithPunctuation(String word,
                                                       Object[][] wordsList,
                                                       Object[][] suffixesList,
                                                       FileWriter out,
                                                       int startIndex, ArrayMode mode) {
        boolean isDecode = mode == ArrayMode.DECODE;
        int indexToEncode = CipherProcessor.getArrayIndex(mode, isDecode);
        //build the result array of elements which have punctuation inside
        String[] withPunctuation = getElementsWithPunctuation(word);
        int wordsListLength = wordsList.length;

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
                System.out.println(partition);
                SuffixProcessor.processSuffixes(partition, wordsList, suffixesList, indexToEncode, mode, wordsListLength,
                        out);
            }
        }

        return false;
    }
}
