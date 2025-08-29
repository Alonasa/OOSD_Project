package ie.atu.sw;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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
    private static final Pattern BY_SUFFIX = Pattern.compile("@@");
    //Modes
    private static final ArrayMode ENCODE_MODE = ArrayMode.ENCODE;
    private static final ArrayMode DECODE_MODE = ArrayMode.DECODE;
    private static final ArrayMode SUFFIXES_MODE = ArrayMode.SUFFIXES;
    private static final ArrayMode WORDS_MODE = ArrayMode.WORDS;
    //ArrayIndexes
    private static final int DECODED_INDEX = 0;
    private static final int ENCODED_INDEX = 1;


    public static void decode(String source, Object[][] array, ArrayMode mode) {
        String outputLocation = mode == ENCODE_MODE ? "./out.txt" : "./out-decoded.txt";
        decode(source, outputLocation, array, mode);
    }


    public static void decode(String source, String output, Object[][] array, ArrayMode mode) {
        try {
            BufferedReader br = getBufferedReader(source);
            FileWriter out = new FileWriter(output, true);
            ArrayGenerator ag = new ArrayGenerator();
            int amountOfLines = ag.getAmountOfLines(source);
            int indexToDecode = mode == DECODE_MODE ? 0 : 1;
            int indexToEncode = mode == ENCODE_MODE ? 0 : 1;
            int linesProcessed = 0;
            boolean encodeMode = mode == ENCODE_MODE;

            String line;

            Object[][] suffixesList = filterArray(array, SUFFIXES_MODE);
            Object[][] wordsList = filterArray(array, WORDS_MODE);
            int wordsListLength = wordsList[indexToEncode].length;
            int suffixesListLength = suffixesList[indexToEncode].length;


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
                                needToFind = decodeExactMatchess(words[wordIndex], wordsList, indexToEncode,
                                        indexToDecode, wordsListLength, out);
                                while (needToFind) {
                                    //build the result array of elements which have punctuation inside
                                    String[] withPunctuation = getElementsWithPunctuation(words[wordIndex]);

                                    if (withPunctuation.length > 0) {
                                        for (String partition : withPunctuation) {
                                            index = 0;
                                            needToFind = true;
                                            while (needToFind && index < wordsListLength) {
                                                //checking if the current part is a number
                                                boolean isNumber = checkNumeric(partition);
                                                if (isNumber) {
                                                    needToFind = decodeNumbers(partition, wordsList, indexToEncode,
                                                            indexToDecode, wordsListLength,
                                                            out);

                                                }
                                                //checking if current element in the map
                                                else if (partition.equals(wordsList[indexToEncode][index])) {
                                                    stringBuilder(wordsList, indexToDecode, index, out);
                                                    needToFind = false;
                                                }

                                                index++;
                                            }

                                            if (needToFind) {
                                                int suffixIndex = getSuffixIndex(suffixesList, words[wordIndex]);
                                                if (suffixIndex > 0) {
                                                    String bareSuffix = (suffixesList[0][suffixIndex]).toString();
                                                    String cleanSuffix = splitSuffix(bareSuffix);
                                                    String[] wordToCheck = splitWord(words[wordIndex],
                                                            cleanSuffix);
                                                    if(wordToCheck.length > 0){
                                                        needToFind = decodeExactMatchess(wordToCheck[0],
                                                                wordsList, indexToEncode,
                                                                indexToDecode, wordsListLength, out);

                                                        if (needToFind) {
                                                            index = 0;
                                                            stringBuilder(wordsList, indexToDecode, index, out);
                                                        }
                                                    }

                                                    stringBuilder(suffixesList, indexToDecode, suffixIndex, out);
                                                } else {
                                                    index = 0;
                                                    stringBuilder(wordsList, indexToDecode, index, out);
                                                }
                                            }
                                        }
                                    } else {
                                        stringBuilder(wordsList, indexToDecode, index, out);
                                    }
                                    needToFind = false;
                                }
                            }
                        }
                    } else {
                        boolean firstEmpty = words[DECODED_INDEX].equals("0");
                        boolean previousEmpty = true;
                        String str = array[indexToDecode][Integer.parseInt(words[wordIndex])].toString();
                        String nextElement = array[indexToDecode][Integer.parseInt(words[(wordIndex + 1 < words.length ?
                                wordIndex + 1 :
                                wordIndex)])].toString();
                        String previousElement = array[indexToDecode][Integer.parseInt(words[(wordIndex - 1 >= 0 ?
                                wordIndex - 1 :
                                wordIndex)])].toString();

                        if (!(words.length == 1 && firstEmpty)) {
                            if (isABoolean(previousElement, previousEmpty, str, nextElement)) {
                                String firstLetter = str.substring(0, 1).toUpperCase(Locale.ROOT);
                                String restOfTheWord = str.substring(1);
                                String capitalized = LoggerUtil.buildWord(firstLetter, restOfTheWord);
                                out.write(capitalized);
                            } else {
                                boolean gotSuffix = checkForSuffix(str);
                                if (gotSuffix) {
                                    String suffix = splitSuffix(str);
                                    out.write(suffix);
                                } else {
                                    out.write(str);
                                }
                                nextNumeric = checkNumeric(nextElement);

                            }
                        } else {
                            previousEmpty = true;
                        }

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

    private static boolean isABoolean(String previousElement, boolean previousEmpty, String str, String nextElement) {
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


    private static boolean isPunctuation(boolean isLongNumber, String nextElement, String str) {
        Matcher matcher = BY_PUNCTUATION.matcher(nextElement);
        boolean matchedByPunctuation = matcher.find();
        return isPunctuation(isLongNumber, nextElement, str, matchedByPunctuation);
    }

    private static boolean isPunctuation(boolean isLongNumber, String nextWord, String word,
                                         boolean matchedByPunctuation) {
        return isLongNumber || !nextWord.equals("[???]") && matchedByPunctuation && !nextWord.equals("(") ||
                word.equals("(") || word.equals("-") || (word.equals(":") && checkNumeric(nextWord)) || word.equals("'");
    }


    private static boolean decodeExactMatchess(String word,
                                               Object[][] wordsList,
                                               int indexToEncode,
                                               int indexToDecode,
                                               int wordsListLength,
                                               FileWriter out) {
        //Searches for a word in a specific list and processes it if found.
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
            needToFind = decodeExactMatchess(number, wordsList, indexToEncode,
                    indexToDecode, wordsListLength, out);
        }
        return needToFind;
    }


    public static int countElementsForSeparation(Object[] elements, ArrayMode mode) {
        //method used to count amount elements for the future filtration in arrays
        int counter = 0;
        for (Object row : elements) {
            String value = row.toString();
            boolean isSuffix = checkForSuffix(value);
            boolean isSuffixMode = isSuffixMode(mode, isSuffix);
            if (isSuffixMode) {
                counter++;
            }
        }
        return counter;
    }


    private static boolean isSuffixMode(ArrayMode mode, boolean isSuffix) {
        return (mode == SUFFIXES_MODE) == isSuffix;
    }


    private static boolean checkForSuffix(String word) {
        String prefix = String.valueOf(BY_SUFFIX);
        return word.startsWith(prefix);
    }


    private static String splitSuffix(String word) {
        return BY_SUFFIX.split(word)[1];
    }


    private static String[] splitWord(String word, String suffix) {
        String[] prefix = word.split(suffix);
        return prefix;
    }


    private static int getSuffixIndex(Object[][] suffixesList, String element) {
        //Iterate over all suffixes and looking for the longest match
        int suffixesLength = suffixesList[0].length;
        int bestSuffixIndex = -1;
        int bestSuffixLength = 0;
        String bestSuffix = "";

        for (int index = 0; index < suffixesLength; index++) {
            String bareSuffix = (suffixesList[0][index]).toString();
            String cleanSuffix = splitSuffix(bareSuffix);
            boolean isSuffixMatch = element.endsWith(cleanSuffix);
            int suffixLength = cleanSuffix.length();
            if (isSuffixMatch && suffixLength > bestSuffixLength) {
                bestSuffixIndex = index;
                bestSuffixLength = suffixLength;
                bestSuffix = cleanSuffix;
            }
        }

        if (bestSuffixIndex >= 0) {
            String bestMatch = (suffixesList[0][bestSuffixIndex]).toString();
//            System.out.println(bestMatch);
//
//            System.out.println(Arrays.deepToString(splitWord(element, bestSuffix)));
        }

        return bestSuffixIndex;
    }


    private static Object[][] filterArray(Object[][] initialArray, ArrayMode mode) {
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
                boolean isSuffix = checkForSuffix(value);

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


    public static void stringBuilder(Object[][] array, int indexToDecode, int index, FileWriter out) {
        try {
            String str = String.valueOf(array[indexToDecode][index]);
            out.write(str);
            out.write(",");
        } catch (IOException e) {
            LoggerUtil.printErrorMessage("", e);
        }
    }


    public static BufferedReader getBufferedReader(String source) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(source),
                    StandardCharsets.UTF_8));
            return br;
        } catch (FileNotFoundException e) {
            LoggerUtil.printErrorMessage("File Error: ", e);
        }
        return null;
    }

}