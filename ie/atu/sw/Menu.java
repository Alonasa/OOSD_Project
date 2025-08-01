package ie.atu.sw;

import java.util.Objects;
import java.util.Scanner;

public class Menu {
    public void renderMenu() throws Exception {
        System.out.println(ConsoleColour.BLUE);
        System.out.println("************************************************************");
        System.out.println("*     ATU - Dept. of Computer Science & Applied Physics    *");
        System.out.println("*                                                          *");
        System.out.println("*              Encoding Words with Suffixes                *");
        System.out.println("*                                                          *");
        System.out.println("************************************************************");
        System.out.println("(1) Specify Mapping File");
        System.out.println("(2) Specify Text File to Encode");
        System.out.println("(3) Specify Output File (default: ./out.txt)");
        System.out.println("(4) Configure Options");
        System.out.println("(5) Encode Text File");
        System.out.println("(6) Decode Text File");
        System.out.println("(7) Optional Extras...");
        System.out.println("(8) Quit");

        System.out.print(ConsoleColour.BLACK_BOLD_BRIGHT);
        System.out.println();
        System.out.print("Select Option [1-8]>: ");
    }

    public boolean processMenuInput(boolean keepRunning) throws Exception {
        Scanner scanner = new Scanner(System.in);
        Object[][] array = null;

        String menuItem = scanner.nextLine();
        switch (menuItem) {
            case "1" -> {
                System.out.println("Add file location, directory included: 1");
                String fileLocation = scanner.nextLine();

            }
            case "2" -> {
                System.out.println("Add file location, directory included: 2");
                String fileLocation = scanner.nextLine();
                FileReader.main(new String[]{fileLocation}, "create");
            }
            case "3" -> {
                System.out.println("Specify Output File (default: ./out.txt): 3");
                String fileLocation = scanner.nextLine();
            }
            case "8" -> {
                System.out.println("Goodbye! 8");
                keepRunning = false;
            }
            default -> {
                System.out.println("Invalid Option Selected");
            }
        }
        return keepRunning;
    }
}