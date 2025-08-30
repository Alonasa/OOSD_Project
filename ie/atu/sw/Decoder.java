package ie.atu.sw;

public class Decoder {
    private static final ArrayMode ENCODE_MODE = ArrayMode.ENCODE;

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder(args[0]);
    }

    public static void decode(String source, Object[][] array, ArrayMode mode) {
        String outputLocation = mode == ENCODE_MODE ? "./out.txt" : "./out-decoded.txt";
        FileReader.processFile(source, outputLocation, array, mode);
    }

    public static void decode(String source, String output, Object[][] array, ArrayMode mode) {

    }
}
