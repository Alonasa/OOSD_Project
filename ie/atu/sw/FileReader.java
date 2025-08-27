package ie.atu.sw;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class FileReader {
    private static final ArrayGenerator ag = new ArrayGenerator();
    private static final Pattern IS_NUMBER = Pattern.compile("\\d+");
    private static final Pattern BY_LETTER = Pattern.compile("");
    private static final Pattern BY_WORD = Pattern.compile("\\s+");
    private static final Pattern BY_COMMA = Pattern.compile(",");
    private static final Pattern WORDS_OR_PUNCTUATION = Pattern.compile("(\\w+|[-\\p{Punct}])");
    private static final Pattern BY_PUNCTUATION = Pattern.compile("\\p{Punct}");
    private static final ArrayMode ENCODE_MODE = ArrayMode.ENCODE;
    private static final ArrayMode DECODE_MODE = ArrayMode.DECODE;
    private static final ArrayMode SUFFIXES_MODE = ArrayMode.SUFFIXES;


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
            ProgressBar pb = new ProgressBar();
            int indexToDecode = mode == DECODE_MODE ? 0 : 1;
            int indexToEncode = mode == ENCODE_MODE ? 0 : 1;
            int arrLength = array[indexToEncode].length;
            int linesProcessed = 0;
            boolean encodeMode = mode == ENCODE_MODE;

            String line;
            boolean nextNumeric = false;


//            Object[][] suffixes = ag.getArray("./encodings-10000/encodings-10000.csv", SUFFIXES_MODE);
//            System.out.println(Arrays.deepToString(suffixes));

            //System.out.println(Arrays.deepToString(array));

            while ((line = br.readLine()) != null) {
                System.out.print(ConsoleColour.YELLOW);//Change the colour of the console text
                pb.printProgress(linesProcessed + 1, amountOfLines);
                String[] words = encodeMode ? BY_WORD.split(line.toLowerCase()) : BY_COMMA.split(line);
                int wordsLength = words.length;

                for (int word = 0; word < wordsLength; word++) {
                    if (encodeMode) {
                        int index = 0;
                        boolean needToFind = true;
                        while (needToFind) {
                            while (needToFind && index < arrLength) {
                                if (words[word].equals(array[indexToEncode][index])) {
                                    stringBuilder(array, indexToDecode, index, out);
                                    needToFind = false;
                                }
                                index++;
                            }

                            while (needToFind) {
                                Matcher matcher = WORDS_OR_PUNCTUATION.matcher(words[word]);

                                int count = 0;
                                while (matcher.find()) {
                                    count++;
                                }
                                String[] resultArray = new String[count];

                                matcher.reset();
                                int indexa = 0;
                                while (matcher.find()) {
                                    resultArray[indexa++] = matcher.group();
                                }

                                if (resultArray.length > 0) {
                                    for (String partition : resultArray) {
                                        index = 0;
                                        needToFind = true;
                                        while (needToFind && index < arrLength) {
                                            if (checkNumeric(partition)) {
                                                String[] numbers = BY_LETTER.split(partition);
                                                for (String number : numbers) {
                                                    index = 0;
                                                    needToFind = true;
                                                    while (needToFind && index < arrLength) {
                                                        if (number.equals(array[indexToEncode][index])) {
                                                            stringBuilder(array, indexToDecode, index, out);
                                                            needToFind = false;
                                                        }
                                                        index++;
                                                    }
                                                }
                                            } else if (partition.equals(array[indexToEncode][index]) && needToFind) {
                                                stringBuilder(array, indexToDecode, index, out);
                                                needToFind = false;
                                            }
                                            index++;
                                        }

                                        if (needToFind) {
                                            index = 0;
                                            stringBuilder(array, indexToDecode, index, out);
                                        }
                                    }
                                } else {
                                    index = 0;
                                    stringBuilder(array, indexToDecode, index, out);

                                }
                                needToFind = false;
                            }
                        }
                    } else {
                        boolean firstEmpty = words[0].equals("0");
                        String str = array[indexToDecode][Integer.parseInt(words[word])].toString();
                        String nextElement = array[indexToDecode][Integer.parseInt(words[(word + 1 < words.length ?
                                word + 1 :
                                word)])].toString();
                        String previousElement = array[indexToDecode][Integer.parseInt(words[(word - 1 >= 0 ?
                                word - 1 :
                                word)])].toString();

                        if (!(words.length == 1 && firstEmpty)) {
                            if (previousElement.equals(".") || str.length() == 1 && nextElement.equals(".")) {
                                String capitalized = str.substring(0, 1).toUpperCase() + str.substring(1);
                                out.write(capitalized);
                            } else {
                                out.write(str);
                                nextNumeric = checkNumeric(nextElement);
                            }
                        }

                        boolean isLongNumber = checkLongNumber(nextNumeric, str);

                        if (isABoolean(isLongNumber, nextElement, str)) {
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

            System.out.println();
            System.out.println("Successfully decoded file");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }


    private static boolean isABoolean(boolean isLongNumber, String nextElement, String str) {
        Matcher matcher = BY_PUNCTUATION.matcher(nextElement);
        boolean matchedByPunctuation = matcher.find();
        return isABoolean(isLongNumber, nextElement, str, matchedByPunctuation);
    }

    private static boolean isABoolean(boolean isLongNumber, String nextElement, String str, boolean matchedByPunctuation) {
        return isLongNumber || !nextElement.equals("[???]") && matchedByPunctuation && !nextElement.equals("(") ||
                str.equals("(") || str.equals("-") || (str.equals(":") && checkNumeric(nextElement)) || str.equals("'");
    }


    public static BufferedReader getBufferedReader(String source) {
        try {
            return new BufferedReader(new InputStreamReader(new FileInputStream(source),
                    StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            String message = e.getMessage();
            System.out.println(message);
        }
        return null;
    }


    public static boolean checkNumeric(String word) {
        return IS_NUMBER.matcher(word).matches();
    }


    public static void stringBuilder(Object[][] array, int indexToDecode, int index, FileWriter out) throws IOException {
        String str = String.valueOf(array[indexToDecode][index]);
        out.write(str);
        out.write(",");
    }


    public static boolean checkLongNumber(boolean previousNumeric, String currentElement) {
        boolean isNumber = checkNumeric(currentElement);
        return previousNumeric && isNumber;
    }


    public int countElementsForSeparation(int counter, String element, ArrayMode mode) {
        boolean isSuffix = checkForSuffix(element);
        if (isSuffixMode(mode, isSuffix)) {
            counter++;
        }
        return counter;
    }


    private boolean isSuffixMode(ArrayMode mode, boolean isSuffix) {
        if (mode == SUFFIXES_MODE) {
            return isSuffix;
        } else {
            return !isSuffix;
        }
    }


    private static boolean checkForSuffix(String word) {
        return word.startsWith("@@");
    }
}