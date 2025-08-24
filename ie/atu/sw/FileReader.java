package ie.atu.sw;

import java.io.*;
import java.util.Arrays;
import java.util.regex.Pattern;

class FileReader {
    private static final String SPECIAL_CHARS = ".*[!&'()*+\\-.,:;=?\\[\\]`_]+.";
    private static final Pattern PATTERN = Pattern.compile(SPECIAL_CHARS);
    private static final Pattern ISNUMBER = Pattern.compile("\\d+");


    public static void decode(String source, Object[][] array, String mode) {
        String outputLocation = mode.equals("encode") ? "./out.txt" : "./out-decoded.txt";
        decode(source, outputLocation, array, mode);
    }

    public static void decode(String source, String output, Object[][] array, String mode) {
        try {
            BufferedReader br = getBufferedReader(source);
            FileWriter out = new FileWriter(output, true);
            ArrayGenerator ag = new ArrayGenerator();
            int amountOfLines = ag.getAmountOfLines(source);
            ProgressBar pb = new ProgressBar();
            int indexToDecode = mode.equals("decode") ? 0 : 1;
            int indexToEncode = mode.equals("encode") ? 0 : 1;
            int arrLength = array[indexToEncode].length;
            int linesProcessed = 0;
            boolean encodeMode = mode.equals("encode");

            String line;
            boolean previousNumeric = false;


            while (null != (line = br.readLine())) {
                System.out.print(ConsoleColour.YELLOW);//Change the colour of the console text
                pb.printProgress(linesProcessed + 1, amountOfLines);
                String[] words = encodeMode ? line.toLowerCase().split("\\s+") : line.split(",");
                for (int word = 0; word < words.length; word++) {
                    if (encodeMode) {
                        int index = 0;
                        boolean needToFind = true;
                        while (needToFind) {
                            while (needToFind && index < arrLength) {
                                if (words[word].equals(array[indexToEncode][index])) {
                                    stringBuilder(array, indexToDecode, index, out);
                                    needToFind = false;
                                    break;
                                }
                                index++;
                            }
                            if (!needToFind) {
                                break;
                            }

                            if (checkNumeric(words[word])) {
                                String[] numbers = words[word].split("");
                                for (String number : numbers) {
                                    index = 0;
                                    needToFind = true;
                                    while (needToFind && index < arrLength) {
                                        if (number.equals(array[indexToEncode][index])) {
                                            stringBuilder(array, indexToDecode, index, out);
                                            needToFind = false;
                                            break;
                                        }
                                        index++;
                                    }
                                }
                            } else {
                                index = 0;
                                stringBuilder(array, indexToDecode, index, out);
                                needToFind = false;
                            }
                        }
                    } else {
                        boolean firstEmpty = "0".equals(words[0]);
                        String str = array[indexToDecode][Integer.parseInt(words[word])].toString();
                        if (!(1 == words.length && firstEmpty)) {
                            out.write(str);
                            previousNumeric =
                                    checkNumeric(array[indexToDecode][Integer.parseInt(words[(word + 1 < words.length ?
                                            word + 1:
                                            word)])].toString());
                        }

                        boolean islongNumber = checkLongNumber(previousNumeric, str);

                        if (islongNumber){
                            out.write("");
                        }else {
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
        return ISNUMBER.matcher(word).matches();
    }

    public static void stringBuilder(Object[][] array, int indexToDecode, int index, FileWriter out) throws IOException {
        String str = String.valueOf(array[indexToDecode][index]);
        out.write(str);
        out.write(",");
    }


    public static boolean checkLongNumber(boolean previousNumeric, String currentElement){
        boolean isNumber = checkNumeric(currentElement);
        return previousNumeric && isNumber;
    }

    //./encodings-10000/encodings-10000.csv
//    ./textfiles/BibleGod.txt


}