package ie.atu.sw;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class CipherProcessor {
    private static final ArrayMode ENCODE_MODE = ArrayMode.ENCODE;
    private static final ArrayMode DECODE_MODE = ArrayMode.DECODE;
    private static final int DECODED_INDEX = 0;


    public static void decode(String source, Object[][] array, ArrayMode mode) {
        String outputLocation = mode == ENCODE_MODE ? "./out.txt" : "./out-decoded.txt";
        FileReader.processFile(source, outputLocation, array, mode);
    }

    public static void decode(String[] words, Object[][] array, int indexToDecode, int wordIndex,
                              FileWriter out, boolean previousLineEmpty) throws IOException {
        int wordsLength = words.length;
        boolean nextNumeric = false;
        boolean firstEmpty = words[DECODED_INDEX].equals("0");


        String str = array[indexToDecode][Integer.parseInt(words[wordIndex])].toString();

        String nextElement = array[indexToDecode][Integer.parseInt(words[(wordIndex + 1 < words.length ?
                wordIndex + 1 :
                wordIndex)])].toString();
        String previousElement = array[indexToDecode][Integer.parseInt(words[(wordIndex - 1 >= 0 ?
                wordIndex - 1 :
                wordIndex)])].toString();

        if (!(wordsLength == 1 && firstEmpty)) {
            if (WordsProcessor.isCapitalized(previousElement, previousLineEmpty, str, nextElement)) {
                String firstLetter = str.substring(0, 1).toUpperCase(Locale.ROOT);
                String restOfTheWord = str.substring(1);
                String capitalized = LoggerUtil.buildWord(firstLetter, restOfTheWord);
                out.write(capitalized);
            } else {
                SuffixProcessor.buildSuffixString(str, out);
                nextNumeric = WordsProcessor.checkNumeric(nextElement);
            }
        }


        boolean isLongNumber = WordsProcessor.checkLongNumber(nextNumeric, str);

        if (WordsProcessor.isPunctuation(isLongNumber, nextElement, str)) {
            out.write("");
        } else {
            out.write(" ");
        }
    }


    public static int getArrayIndex(ArrayMode mode, boolean isDecode) {
        return isModeMatch(mode, isDecode) ? 0 : 1;
    }

    private static boolean isModeMatch(ArrayMode mode, boolean isDecode) {
        return (isDecode && mode == ArrayMode.DECODE) || (!isDecode && mode == ArrayMode.ENCODE);
    }

}
