package ie.atu.sw;

import java.io.*;
import java.util.Arrays;

class FileReader {
    public static void main(String[] args, String mode) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(args[0])));
        FileWriter out = new FileWriter("output.txt");
        String next;

        if (mode.equals("create")) {
            Object[] sizes = getArrayLength(br);
            createArray(args[0], sizes);
            System.out.println("Successfully created array");
        }

        while ((next = br.readLine()) != null) {
            out.write(next);
            out.write("\n");
            System.out.println(next);
        }

        out.flush();
        out.close();
        br.close();

    }

    //./encodings-10000/encodings-10000.csv

    private static Object[] getArrayLength(BufferedReader br) throws Exception {
        short arrayLength = 2;
        int amountOfElements = 0;
        while (br.readLine() != null) amountOfElements++;

        Object[] sizes = new Object[arrayLength];

        sizes[0] = amountOfElements;
        sizes[1] = arrayLength;
        return sizes;
    }


    private static Object[][] createArray(String file, Object[] sizes) throws Exception {
        int amountOfElements = (Integer) sizes[0];
        short arrayLength = (Short) sizes[1];
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));


        Object[][] array = new Object[arrayLength][amountOfElements];

        String line;
        int i = 0;

        while ((line = br.readLine()) != null) {
                Object[] newLine = line.split(",");
                array[0][i] = newLine[0];
                array[1][i] = newLine[1];
            i++;
        }

        br.close();
        return array;
    }
}