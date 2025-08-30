package ie.atu.sw;
import java.util.Arrays;

public class LoggerUtil {
    private LoggerUtil() {
    }

    public static String buildString(Object... args){
        StringBuilder messageString = new StringBuilder(4);
        for (Object arg: args){
            messageString.append(" ").append(arg);
        }

        return messageString.toString().trim();
    }

    public static String buildWord(Object... args){
        StringBuilder messageString = new StringBuilder(4);
        for (Object arg: args){
            messageString.append(arg);
        }

        return messageString.toString().trim();
    }

    public static void printErrorMessage(String format, Object... args) {
        StringBuilder message = new StringBuilder(format);

        for (Object arg : args) {
            if (arg instanceof Throwable throwable) {
                String errorMessage = throwable.getMessage();
                // Appended properly spaced error message
                message.append(" ").append(errorMessage);
            } else {
                message.append(" ").append(arg);
            }
        }

        String messageToPrint = message.toString().trim();
        // Use System.err for errors to better distinguish error messages
        System.err.println(messageToPrint);
    }

    public static int convertToNumber(String element) {
        int defaultValue = 0;
        if (element == null || element.isEmpty()) {
            printErrorMessage("Invalid input: null or empty string. Returning default value 0.");
            return defaultValue; // Default fallback value
        }

        try {
            return Integer.parseInt(element);
        } catch (NumberFormatException e) {
            printErrorMessage("Unable to parse '%s' as an integer. Returning default value 0.", element);
            return defaultValue; // Default fallback value
        }

    }
}
