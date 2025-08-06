package ie.atu.sw;

import java.io.*;
import java.util.Arrays;

class FileReader {
    public static void main(String[] args, String mode) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(args[0])));
        FileWriter out = new FileWriter("output.txt");

        String next;

//        if (mode.equals("create")) {
//            Object[] sizes = getArrayLength(br);
//            Object[][] encodings = createArray(args[0], sizes);
//            System.out.println("Successfully created array");
//            System.out.println(Arrays.deepToString(encodings));
//        }

        while ((next = br.readLine()) != null) {
            out.write(next);
            out.write("\n");
            System.out.println(next);
        }

        out.flush();
        out.close();
        br.close();

    }

    public static void decode(String source, String[][] array, String mode) {
        String outputLocation = "./out.txt";
        decode(source, outputLocation, array, mode);
    }

    public static void decode(String source, String output, String[][] array, String mode) {
        try {
            BufferedReader br = getBufferedReader(source);
            FileWriter out = new FileWriter(output, true);
            ArrayGenerator ag = new ArrayGenerator();
            int amountOfLines = ag.getAmountOfLines(source);
            ProgressBar pb = new ProgressBar();
            int indexToEncode = mode.equals("decode") ? 1 : 0;
            int indexToDecpde = mode.equals("decode") ? 0 : 1;
            int linesProcessed = 0;

            String line;

            while ((line = br.readLine()) != null) {
                System.out.print(ConsoleColour.YELLOW);    //Change the colour of the console text
                pb.printProgress(linesProcessed + 1, amountOfLines);
                String[] words = line.toLowerCase().split("\\s+");
                for (String word : words) {
                    int index = 0;
                    for (int i = 0; i < array[0].length; i++) {
                        if (word.equals(array[0][i])) {
                            index = i;
                        }
                    }
                    out.write(array[1][index]);
                    out.write(",");
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