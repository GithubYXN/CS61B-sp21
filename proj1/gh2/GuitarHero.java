package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {
    public static final double CONCERT = 440.0;
    static String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";

    public static void main(String[] args) {
        int capacity = keyboard.length();
        GuitarString[] guitars = new GuitarString[capacity];
        for (int i = 0; i < capacity; i++) {
            guitars[i] = new GuitarString(CONCERT * Math.pow(2, (double) (i - 24) / 12));
        }

        while (true) {

            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int idx = keyboard.indexOf(key);
                if (idx != -1) {
                    guitars[idx].pluck();
                }
            }

            double sample = 0.0;
            for (int i = 0; i < capacity; i++) {
                sample += guitars[i].sample();
            }

            StdAudio.play(sample);

            for (int i = 0; i < capacity; i++) {
                guitars[i].tic();
            }
        }
    }
}
