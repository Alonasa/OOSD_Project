package ie.atu.sw;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileReader {
    //Regex patterns
    private static final Pattern IS_NUMBER = Pattern.compile("\\d+");
    private static final Pattern BY_ELEMENT = Pattern.compile("");
    private static final Pattern BY_WORD = Pattern.compile("\\s+");
    private static final Pattern BY_COMMA = Pattern.compile(",");
    private static final Pattern WORDS_AND_PUNCTUATION = Pattern.compile("(\\w+|[-\\p{Punct}])");
    private static final Pattern BY_PUNCTUATION = Pattern.compile("\\p{Punct}");
    //Modes
    private static final ArrayMode ENCODE_MODE = ArrayMode.ENCODE;
    private static final ArrayMode DECODE_MODE = ArrayMode.DECODE;
    public static final ArrayMode SUFFIXES_MODE = ArrayMode.SUFFIXES;
    private static final ArrayMode WORDS_MODE = ArrayMode.WORDS;
    //ArrayIndexes
    private static final int DECODED_INDEX = 0;

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

            String line;

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
                boolean nextNumeric = false;


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
                                stringBuilder(wordsList, indexToDecode, index, out);
                            } else {
                                // Try to find an exact match first
                                needToFind = isExactMatch(words[wordIndex], wordsList, indexToEncode,
                                        indexToDecode, wordsListLength, out);

                                // If no exact match found, process words with punctuation
                                if (needToFind) {
                                    needToFind = processWordsWithPunctuation(words[wordIndex], wordsList, suffixesList,
                                            indexToEncode, indexToDecode, wordsListLength, out, index);
                                }
                            }
                        }
                    } else {
                        boolean firstEmpty = words[DECODED_INDEX].equals("0");
                        String str = array[indexToDecode][Integer.parseInt(words[wordIndex])].toString();
                        String nextElement = array[indexToDecode][Integer.parseInt(words[(wordIndex + 1 < words.length ?
                                wordIndex + 1 :
                                wordIndex)])].toString();
                        String previousElement = array[indexToDecode][Integer.parseInt(words[(wordIndex - 1 >= 0 ?
                                wordIndex - 1 :
                                wordIndex)])].toString();

                        if (!(words.length == 1 && firstEmpty)) {
                            if (isCapitalized(previousElement, previousLineEmpty, str, nextElement)) {
                                String firstLetter = str.substring(0, 1).toUpperCase(Locale.ROOT);
                                String restOfTheWord = str.substring(1);
                                String capitalized = LoggerUtil.buildWord(firstLetter, restOfTheWord);
                                out.write(capitalized);
                            } else {
                                SuffixProcessor.buildSuffixString(str, out);
                                nextNumeric = checkNumeric(nextElement);

                            }
                        }

                        previousLineEmpty = line.length() == 2;

                        boolean isLongNumber = checkLongNumber(nextNumeric, str);

                        if (isPunctuation(isLongNumber, nextElement, str)) {
                            out.write("");
                        } else {
                            out.write(" ");
                        }
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

    private static boolean isCapitalized(String previousElement, boolean previousEmpty, String str, String nextElement) {
        return previousElement.equals(".") || previousEmpty || str.length() == 1 && nextElement.equals(".");
    }

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


    private static boolean isPunctuation(boolean isLongNumber, String nextElement, String currentElement) {
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
                                int indexToDecode,
                                int wordsListLength,
                                FileWriter out) {
        // Searches for a word in a specific list and processes it if found.
        boolean needToFind = true;
        int index = 0;

        while (needToFind && index < wordsListLength) {
            if (word.equals(wordsList[indexToEncode][index])) {
                //add found word to the resulting file
                stringBuilder(wordsList, indexToDecode, index, out);
                needToFind = false;
            }
            index++;
        }

        return needToFind; // Indicates whether the word was found and processed
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

    private static boolean decodeNumbers(String partition,
                                         Object[][] wordsList,
                                         int indexToEncode,
                                         int indexToDecode,
                                         int wordsListLength,
                                         FileWriter out) {
        String[] numbers = BY_ELEMENT.split(partition);
        boolean needToFind = true;
        for (String number : numbers) {
            needToFind = isExactMatch(number, wordsList, indexToEncode,
                    indexToDecode, wordsListLength, out);
        }
        return needToFind;
    }


    private static boolean processWordsWithPunctuation(String word,
                                                       Object[][] wordsList,
                                                       Object[][] suffixesList,
                                                       int indexToEncode,
                                                       int indexToDecode,
                                                       int wordsListLength,
                                                       FileWriter out,
                                                       int startIndex) {
        //build the result array of elements which have punctuation inside
        String[] withPunctuation = getElementsWithPunctuation(word);

        if (withPunctuation.length == 0) {
            stringBuilder(wordsList, indexToDecode, startIndex, out);
            return false;
        }

        for (String partition : withPunctuation) {
            int index = 0;
            boolean needToFind = true;
            while (needToFind && index < wordsListLength) {
                //checking if the current part is a number
                if (checkNumeric(partition)) {
                    needToFind = decodeNumbers(partition, wordsList, indexToEncode,
                            indexToDecode, wordsListLength, out);
                }
                //checking if current element in the map
                else if (partition.equals(wordsList[indexToEncode][index])) {
                    stringBuilder(wordsList, indexToDecode, index, out);
                    needToFind = false;
                }
                index++;
            }

            if (needToFind) {
                SuffixProcessor.processSuffixes(word, wordsList, suffixesList, indexToEncode, indexToDecode, wordsListLength, out);
            }
        }

        return false;
    }


    public static void stringBuilder(Object[][] array, int indexToDecode, int index, FileWriter out) {
        //Writes an element from the array to the output file with a comma separator
        try {
            String str = String.valueOf(array[indexToDecode][index]);
            out.write(str);
            out.write(",");
        } catch (IOException e) {
            LoggerUtil.printErrorMessage("Error writing to output file: ", e);
        }
    }


    public static BufferedReader getBufferedReader(String source) {
        // Creates a BufferedReader for the specified source file
        try {
            return new BufferedReader(new InputStreamReader(
                    new FileInputStream(source), StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            LoggerUtil.printErrorMessage("File not found: " + source, e);
            return null;
        }
    }

}