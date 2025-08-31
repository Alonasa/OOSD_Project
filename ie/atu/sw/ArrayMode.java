package ie.atu.sw;

/**
 * The ArrayMode enum represents various operational modes
 * that can be used within a context such as encoding and decoding arrays,
 * as well as processing suffixes and words. Each enum constant
 * is associated with a specific string representation.
 * Made by sample from console colour
 */

public enum ArrayMode {
    ENCODE("encode"),
    DECODE("decode"),
    SUFFIXES("suffixes"),
    WORDS("words");

    private final String mode;

    ArrayMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    @Override
    public String toString() {
        return mode;
    }
}