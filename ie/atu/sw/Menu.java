package ie.atu.sw;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import static java.lang.System.in;
import static java.lang.System.out;

public class Menu {
    private final ArrayGenerator ag = new ArrayGenerator();
    private final Scanner scanner;
    private static final ArrayMode ENCODE_MODE = ArrayMode.ENCODE;
    private static final ArrayMode DECODE_MODE = ArrayMode.DECODE;


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
        out.println("(6) Quit");

        out.print(ConsoleColour.BLACK_BOLD_BRIGHT);
        out.println();
        out.print("Select Option [1-6]>: ");
    }

    String mapFileLocation = "./encodings-10000/encodings-10000.csv";
    String inputFileLocation = "./textfiles/BibleGod.txt";
    String outputFileLocation = "";

    public Menu() {
        scanner = new Scanner(in, StandardCharsets.UTF_8);
    }

    public boolean processMenuInput(boolean keepRunning) {
        try {
            String next = scanner.next();
            int menuItem = LoggerUtil.convertToNumber(next);

            Object[][] mapItems;
            switch (menuItem) {
                case 1 -> {
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
                    mapItems = ag.getArray(mapFileLocation);
                    if (outputFileLocation.isEmpty()) {
                        FileReader.decode(inputFileLocation, mapItems, ENCODE_MODE);
                    } else {
                        FileReader.decode(inputFileLocation, outputFileLocation, mapItems, ENCODE_MODE);
                    }
                }
                case 5 -> {
                    out.println("Begin Decoding");
                    out.println("Mapping file location: " + mapFileLocation);
                    if (mapFileLocation.isEmpty()) {
                        out.println("No mapping file specified");
                    }
                    if (inputFileLocation.isEmpty()) {
                        out.println("No input file specified");
                    }
                    mapItems = ag.getArray(mapFileLocation);
                    FileReader.decode("./out.txt", mapItems, DECODE_MODE);
                }
                case 6 -> {
                    out.println("Goodbye! 6");
                    keepRunning = false;
                }
                default -> {
                    out.println("Invalid Option Selected");
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid number. Please make sure that numbers in range 1-6");
        }
        return keepRunning;
    }
}