package ie.atu.sw;
import java.util.Scanner;

import static java.lang.System.in;
import static java.lang.System.out;

public class Menu {
    private final ArrayGenerator ag = new ArrayGenerator();
    private final Scanner = s;

    public void renderMenu() throws Exception {
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

    Object[][] array;
    String mapFileLocation = "./encodings-10000/encodings-10000.csv";
    String inputFileLocation = "./textfiles/BibleGod.txt";
    String outputFileLocation = "";

    public Menu(){
        s = new Scanner(in, StandardCharsets.UTF_8);
    }

    public boolean processMenuInput(boolean keepRunning) throws Exception {
        final Scanner scanner = new Scanner(in, StandardCharsets.UTF_8);

        String menuItem = Integer.parseInt(s.next());

        switch (menuItem) {
            case "1" -> {
                // ./encodings-10000/encodings-10000.csv
                out.println("Please enter the location of the mapping file");
                this.mapFileLocation = scanner.nextLine();
            }
            case "2" -> {
                out.println("Please enter the location of the input file");
                this.inputFileLocation = scanner.nextLine();
            }
            case "3" -> {
                out.println("Please enter the location of the output file");
                this.outputFileLocation = scanner.nextLine();
            }
            case "4" -> {
                out.println("Begin Encoding");
                out.println("Mapping file location: " + this.mapFileLocation);
                if (this.mapFileLocation.isEmpty()) {
                    out.println("No mapping file specified");
                }
                if (this.inputFileLocation.isEmpty()) {
                    out.println("No input file specified");
                }
                this.array = this.ag.getArray(this.mapFileLocation);
                if (this.outputFileLocation.isEmpty()) {
                    FileReader.decode(this.inputFileLocation, this.array, "encode");
                }else{
                    FileReader.decode(this.inputFileLocation, this.outputFileLocation, this.array, "encode");
                }
            }
            case "5" -> {
                out.println("Begin Decodding");
                out.println("Mapping file location: " + this.mapFileLocation);
                if (this.mapFileLocation.isEmpty()) {
                    out.println("No mapping file specified");
                }
                if (this.inputFileLocation.isEmpty()) {
                    out.println("No input file specified");
                }
                array = this.ag.getArray(this.mapFileLocation);
                FileReader.decode("./out.txt", this.array, "decode");
            }
            case "7" -> {
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