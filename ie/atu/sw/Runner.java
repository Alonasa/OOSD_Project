package ie.atu.sw;

import static java.lang.System.out;

public class Runner {
    private static final Menu menu = new Menu();
    private static boolean keepRunning = true;

    public static void main(String[] args){
        try {
            while (keepRunning) {
                //Output a menu of options and solicit text from the user
                menu.renderMenu();
                keepRunning = menu.processMenuInput(keepRunning);
            }

            Thread.sleep(1000);
        } catch (InterruptedException e) {
            String message = e.getMessage();
            System.err.println(message);
        }
    }
}