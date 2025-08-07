package ie.atu.sw;

import java.io.*;
import java.util.regex.Pattern;

class FileReader {
    private static final Pattern PATTERN = Pattern.compile(".*[,.!]+$");


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
            final int arrLength = array[indexToEncode].length;
            int linesProcessed = 0;
            final boolean encodeMode = mode.equals("encode");

            String line;

            while (null != (line = br.readLine())) {
                System.out.print(ConsoleColour.YELLOW);    //Change the colour of the console text
                pb.printProgress(linesProcessed + 1, amountOfLines);
                String[] words = encodeMode ? line.toLowerCase().split("\\s+") : line.split(",");
                for (String word : words) {
                    if (encodeMode) {
                        int index = 0;
                        for (int i = 0; i < arrLength; i++) {
                            if (word.equals(array[indexToEncode][i])) {
                                index = i;
                            }
                        }
                        String str = String.valueOf(array[indexToDecode][index]);
                        out.write(str);
                        out.write(",");
                        if (PATTERN.matcher(word).matches()) {

                        }
                    } else {
                        if ((1 == words.length) && "0".equals(word)) {
                        } else {
                            final String str = array[indexToDecode][Integer.parseInt(word)].toString();
                            out.write(str);
                        }
                        out.write(" ");
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

    //./encodings-10000/encodings-10000.csv
//    ./textfiles/BibleGod.txt


}