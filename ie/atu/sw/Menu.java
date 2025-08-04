package ie.atu.sw;

import java.util.Objects;
import java.util.Scanner;

import static java.lang.System.*;

@SuppressWarnings("ALL")
public class Menu {
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
        out.println("(4) Configure Options");
        out.println("(5) Encode Text File");
        out.println("(6) Decode Text File");
        out.println("(7) Optional Extras...");
        out.println("(8) Quit");

        out.print(ConsoleColour.BLACK_BOLD_BRIGHT);
        out.println();
        out.print("Select Option [1-8]>: ");
    }

    public boolean processMenuInput(boolean keepRunning) throws Exception {
        Scanner scanner = new Scanner(in);
        Object[][] array = null;

        String menuItem = scanner.nextLine();
        switch (menuItem) {
            case "1" -> {
                out.println("Add file location, directory included: 1");
                String fileLocation = scanner.nextLine();

            }
            case "2" -> {
                out.println("Add file location, directory included: 2");
                String fileLocation = scanner.nextLine();
                FileReader.main(new String[]{fileLocation}, "create");
            }
            case "3" -> {
                out.println("Specify Output File (default: ./out.txt): 3");
                String fileLocation = scanner.nextLine();
            }
            case "8" -> {
                out.println("Goodbye! 8");
                keepRunning = false;
            }
            default -> {
                out.println("Invalid Option Selected");
            }
        }
        return keepRunning;
    }
}