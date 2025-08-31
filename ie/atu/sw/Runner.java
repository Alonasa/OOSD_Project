package ie.atu.sw;

/**
 * The entry point for the application.
 * It controls the flow of program execution, continuously displaying
 * a menu of options to the user, accepting inputs, and executing
 * corresponding actions.
 * The program terminates when the user chooses to exit.
 */
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

            long millisecondsSleep = 1000L;
            Thread.sleep(millisecondsSleep);
        } catch (InterruptedException e) {
            String message = e.getMessage();
            System.err.println(message);
        }
    }
}