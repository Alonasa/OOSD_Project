package ie.atu.sw;

import java.util.Arrays;

public class LoggerUtil {
    private LoggerUtil() {
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
        if (element == null || element.isEmpty()) {
            printErrorMessage("Invalid input: null or empty string. Returning default value 0.");
            return 0; // Default fallback value
        }

        try {
            return Integer.parseInt(element);
        } catch (NumberFormatException e) {
            printErrorMessage("Unable to parse '%s' as an integer. Returning default value 0.", element);
            return 0; // Default fallback value
        }

    }

    public static int convertToNumber(Object element) {
        String arrayElement = Arrays.toString((int[]) element);
        return Integer.parseInt(arrayElement);
    }
}
