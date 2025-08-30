package ie.atu.sw;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileReader {
    //Regex patterns
    private static final Pattern BY_WORD = Pattern.compile("\\s+");
    private static final Pattern BY_COMMA = Pattern.compile(",");
    private static final Pattern WORDS_AND_PUNCTUATION = Pattern.compile("(\\w+|[-\\p{Punct}])");
    //Modes
    private static final ArrayMode ENCODE_MODE = ArrayMode.ENCODE;
    private static final ArrayMode DECODE_MODE = ArrayMode.DECODE;
    public static final ArrayMode SUFFIXES_MODE = ArrayMode.SUFFIXES;
    private static final ArrayMode WORDS_MODE = ArrayMode.WORDS;


    public static void processFile(String source, String output, Object[][] array, ArrayMode mode) {
        try {
            BufferedReader br = getBufferedReader(source);
            FileWriter out = new FileWriter(output, true);
            ArraysProcessor ag = new ArraysProcessor();
            int amountOfLines = ag.getAmountOfLines(source);
            int indexToDecode = mode == DECODE_MODE ? 0 : 1;
            int indexToEncode = mode == ENCODE_MODE ? 0 : 1;
            int linesProcessed = 0;
            boolean encodeMode = mode == ENCODE_MODE;

            String line = null;

            Object[][] suffixesList = ArraysProcessor.filterArray(array, SUFFIXES_MODE);
            Object[][] wordsList = ArraysProcessor.filterArray(array, WORDS_MODE);
            int wordsListLength = wordsList[indexToEncode].length;
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
                        int index = 0;
                        boolean needToFind = true;
                        while (needToFind) {
                            //send current to check if it exists in a mapping file
                            //if yes than we process to the next word otherwise going for deeper search
                            if (words[wordIndex].isEmpty()) {
                                needToFind = false;
                                stringBuilder(wordsList, mode, index, out);
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
            LoggerUtil.printErrorMessage("I/O Error on decoding: ", e);
        }
    }


//    private static String[] getElementsWithPunctuation(String element) {
//        Matcher matcher = WORDS_AND_PUNCTUATION.matcher(element);
//        int count = 0;
//        while (matcher.find()) {
//            count++;
//        }
//        String[] resultArray = new String[count];
//
//        matcher.reset();
//        int matchesCount = 0;
//        while (matcher.find()) {
//            resultArray[matchesCount++] = matcher.group();
//        }
//
//        return resultArray;
//    }


//    private static boolean processWordsWithPunctuation(String word,
//                                                       Object[][] wordsList,
//                                                       Object[][] suffixesList,
//                                                       int indexToEncode,
//                                                       int indexToDecode,
//                                                       int wordsListLength,
//                                                       FileWriter out,
//                                                       int startIndex) {
//        //build the result array of elements which have punctuation inside
//        String[] withPunctuation = getElementsWithPunctuation(word);
//
//        if (withPunctuation.length == 0) {
//            stringBuilder(wordsList, indexToDecode, startIndex, out);
//            return false;
//        }
//
//        for (String partition : withPunctuation) {
//            int index = 0;
//            boolean needToFind = true;
//            while (needToFind && index < wordsListLength) {
//                //checking if the current part is a number
//                if (WordsProcessor.checkNumeric(partition)) {
//                    needToFind = WordsProcessor.isNumbersdecoded(partition, wordsList, indexToEncode,
//                            indexToDecode, wordsListLength, out);
//                }
//                //checking if current element in the map
//                else if (partition.equals(wordsList[indexToEncode][index])) {
//                    stringBuilder(wordsList, indexToDecode, index, out);
//                    needToFind = false;
//                }
//                index++;
//            }
//
//            if (needToFind) {
//                SuffixProcessor.processSuffixes(word, wordsList, suffixesList, indexToEncode, indexToDecode, wordsListLength, out);
//            }
//        }
//
//        return false;
//    }


    /**
     * @param array the array of elements to process
     * @param mode current mode
     * @param index index of the current element
     * @param out the FileWriter, which attach string and comma after each index
     */
    public static void stringBuilder(Object[][] array, ArrayMode mode, int index, FileWriter out) {
        //Writes an element from the array to the output file with a comma separator
        boolean isDecodeMode = mode == ArrayMode.ENCODE;
        int indexToDecode = CipherProcessor.getArrayIndex(mode, isDecodeMode);
        try {
            String str = String.valueOf(array[indexToDecode][index]);
            out.write(str);
            out.write(",");
        } catch (IOException e) {
            LoggerUtil.printErrorMessage("Error writing to output file: ", e);
        }
    }


    /**
     * @param source - address of the source to process
     * @return BufferedReader or null
     */
    public static BufferedReader getBufferedReader(String source) {
        // Creates a BufferedReader for the specified source file
        BufferedReader result = null;

        try {
            result = new BufferedReader(new InputStreamReader(
                    new FileInputStream(source), StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            LoggerUtil.printErrorMessage("File not found: " + source, e);
        }
        return result;
    }

}