package ie.atu.sw;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;


public class SuffixProcessor {
    private static final Pattern BY_SUFFIX = Pattern.compile("@@");

    public static void buildSuffixString(String str, BufferedWriter out) throws IOException {
        boolean gotSuffix = checkForSuffix(str);
        if (gotSuffix) {
            String suffix = splitSuffix(str);
            out.write(suffix);
        } else {
            out.write(str);
        }
    }


    public static void processSuffixes(String word,
                                        Object[][] wordsList,
                                        Object[][] suffixesList,
                                        int indexToEncode,
                                        ArrayMode mode,
                                        int wordsListLength,
                                        BufferedWriter out) {
        //Method for the processing suffixes.
        // If no match returns -1 and sends element 0 to the string builder
        int ZERO_ELEMENT = 0;
        int suffixIndex = getSuffixIndex(suffixesList, word);
        if (suffixIndex > ZERO_ELEMENT) {
            //If matched, separate the suffix from @@,
            // splits it with the rest of the word
            String bareSuffix = (suffixesList[ZERO_ELEMENT][suffixIndex]).toString();
            String cleanSuffix = splitSuffix(bareSuffix);
            String[] wordToCheck = splitWord(word, cleanSuffix);
            if (wordToCheck.length > ZERO_ELEMENT) {
                boolean needToFind = WordsProcessor.isExactMatch(wordToCheck[ZERO_ELEMENT],
                        wordsList, indexToEncode,
                        mode, wordsListLength, out);

                if (needToFind) {
                    FileReader.stringBuilder(wordsList, mode, ZERO_ELEMENT, out);
                }
            }

            FileReader.stringBuilder(suffixesList, mode, suffixIndex, out);
        } else {
            FileReader.stringBuilder(wordsList, mode, ZERO_ELEMENT, out);
        }
    }


    private static String[] splitWord(String word, String suffix) {
        return word.split(suffix);
    }


    public static boolean checkForSuffix(String word) {
        String prefix = String.valueOf(BY_SUFFIX);
        return word.startsWith(prefix);
    }


    private static String splitSuffix(String word) {
        return BY_SUFFIX.split(word)[1];
    }


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
