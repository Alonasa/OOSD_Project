package ie.atu.sw;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class FileReader {
    private static final Pattern IS_NUMBER = Pattern.compile("\\d+");
    private static final Pattern BY_LETTER = Pattern.compile("");
    private static final Pattern BY_WORD = Pattern.compile("\\s+");
    private static final Pattern BY_COMMA = Pattern.compile(",");
    private static final Pattern WORDS_OR_PUNCTUATION = Pattern.compile("(\\w+|[-\\p{Punct}])");
    private static final String ENCODE_MODE = "encode";
    private static final String DECODE_MODE = "decode";


    public static void decode(String source, Object[][] array, String mode) {
        String outputLocation = mode.equals(ENCODE_MODE) ? "./out.txt" : "./out-decoded.txt";
        decode(source, outputLocation, array, mode);
    }

    public static void decode(String source, String output, Object[][] array, String mode) {
        try {
            BufferedReader br = getBufferedReader(source);
            FileWriter out = new FileWriter(output, true);
            ArrayGenerator ag = new ArrayGenerator();
            int amountOfLines = ag.getAmountOfLines(source);
            ProgressBar pb = new ProgressBar();
            int indexToDecode = mode.equals(DECODE_MODE) ? 0 : 1;
            int indexToEncode = mode.equals(ENCODE_MODE) ? 0 : 1;
            int arrLength = array[indexToEncode].length;
            int linesProcessed = 0;
            boolean encodeMode = mode.equals(ENCODE_MODE);

            String line;
            boolean nextNumeric = false;


            while (null != (line = br.readLine())) {
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


                                if (0 < resultArray.length) {
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
                        String previousElement = array[indexToDecode][Integer.parseInt(words[(word - 1 > 0 ?
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

                        if (isLongNumber || nextElement.equals("-") || str.equals("-") || nextElement.equals(".") || nextElement.equals(")") || str.equals("(")) {
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
            throw new RuntimeException(e);
        }

    }

    public static BufferedReader getBufferedReader(String source) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(source)));
        return br;
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

    //./encodings-10000/encodings-10000.csv
//    ./textfiles/BibleGod.txt


}