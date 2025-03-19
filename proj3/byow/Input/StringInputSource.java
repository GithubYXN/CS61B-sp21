package byow.Input;

public class StringInputSource implements InputSource {

    private String input;
    private int index;

    public StringInputSource(String input) {
        this.input = input;
        this.index = 0;
    }

    @Override
    public char getNextKey() {
        char key = input.charAt(index);
        index += 1;
        return Character.toUpperCase(key);
    }

    @Override
    public boolean hasNextKey() {
        return index < input.length();
    }
}
