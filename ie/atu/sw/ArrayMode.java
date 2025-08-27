package ie.atu.sw;

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