package ie.atu.sw;

import static java.lang.System.out;

public class Runner {
    private static final Menu menu = new Menu();
    private static boolean keepRunning = true;

    public static void main(String[] args){
        try {
            while (keepRunning) {
                menu.renderMenu();
                keepRunning = menu.processMenuInput(keepRunning);
            }

            //Output a menu of options and solicit text from the user

            Thread.sleep(1000);
        } catch (Exception e) {
            out.println("Something went wrong: " + e.getMessage());
        }
    }

    public static void saveArray(String fileLocation) {

    }
}