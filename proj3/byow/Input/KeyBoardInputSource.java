package byow.Input;

import edu.princeton.cs.introcs.StdDraw;

public class KeyBoardInputSource implements InputSource {

    public KeyBoardInputSource() {

    }

    @Override
    public char getNextKey() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                return Character.toUpperCase(StdDraw.nextKeyTyped());
            }
        }
    }

    @Override
    public boolean hasNextKey() {
        return true;
    }
}
