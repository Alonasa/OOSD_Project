package ie.atu.sw;
import java.util.Scanner;

import static java.lang.System.in;
import static java.lang.System.out;

public class Menu {
    private final ArrayGenerator ag = new ArrayGenerator();
    private final Scanner s;
    private Object[][] array;


    public void renderMenu() {
        out.println(ConsoleColour.BLUE);
        out.println("************************************************************");
        out.println("*     ATU - Dept. of Computer Science & Applied Physics    *");
        out.println("*                                                          *");
        out.println("*               Encoding Words with Suffixes               *");
        out.println("************************************************************");
        out.println("(1) Specify Mapping File");
        out.println("(2) Specify Text File to Encode");
        out.println("(3) Specify Output File (default: ./out.txt)");
        out.println("(4) Encode Text File");
        out.println("(5) Decode Text File");
        out.println("(6) Optional Extras...");
        out.println("(7) Quit");

        out.print(ConsoleColour.BLACK_BOLD_BRIGHT);
        out.println();
        out.print("Select Option [1-8]>: ");
    }
    String mapFileLocation = "./encodings-10000/encodings-10000.csv";
    String inputFileLocation = "./textfiles/BibleGod.txt";
    String outputFileLocation = "";

    public Menu() {
        s = new Scanner(in);
    }

    public boolean processMenuInput(boolean keepRunning) throws Exception {
        int menuItem = Integer.parseInt(s.next());

        switch (menuItem) {
            case 1 -> {
                // ./encodings-10000/encodings-10000.csv
                out.println("Please enter the location of the mapping file");
            }
            case 2 -> {
                out.println("Please enter the location of the input file");
            }
            case 3 -> {
                out.println("Please enter the location of the output file");
            }
            case 4 -> {
                out.println("Begin Encoding");
                out.println("Mapping file location: " + mapFileLocation);
                if (mapFileLocation.isEmpty()) {
                    out.println("No mapping file specified");
                }
                if (inputFileLocation.isEmpty()) {
                    out.println("No input file specified");
                }
                array = ag.getArray(mapFileLocation);
                if (outputFileLocation.isEmpty()) {
                    FileReader.decode(inputFileLocation, array, "encode");
                }else{
                    FileReader.decode(inputFileLocation, outputFileLocation, array, "encode");
                }
            }
            case 5 -> {
                out.println("Begin Decodding");
                out.println("Mapping file location: " + mapFileLocation);
                if (mapFileLocation.isEmpty()) {
                    out.println("No mapping file specified");
                }
                if (inputFileLocation.isEmpty()) {
                    out.println("No input file specified");
                }
                array = ag.getArray(mapFileLocation);
                FileReader.decode("./out.txt", array, "decode");
            }
            case 6 -> {
                out.println("Goodbye! 7");
                keepRunning = false;
            }
            default -> {
                out.println("Invalid Option Selected");
            }
        }
        return keepRunning;
    }
}