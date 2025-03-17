package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        this.rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        String s = "";
        for (int i = 0; i < n; i++) {
            s += CHARACTERS[rand.nextInt(CHARACTERS.length)];
        }
        return s;
    }

    public void drawFrame(String s, String state) {
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.clear(Color.BLACK);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 20));
        StdDraw.line(0, height - 2, width, height - 2);
        StdDraw.textLeft(1, height - 1, "Round: " + round);
        StdDraw.text((double) width / 2, height - 1, state);
        String encouragement = ENCOURAGEMENT[rand.nextInt(ENCOURAGEMENT.length)];
        StdDraw.textRight(width - 1, height - 1, encouragement);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
        StdDraw.text((double) width / 2, (double) height / 2, s);
        StdDraw.show();
    }

    public void flashSequence(String letters) {
        int idx = 0;
        while (idx < letters.length()) {
            drawFrame(Character.toString(letters.charAt(idx)), "Watch!");
            idx += 1;
            StdDraw.pause(1000);
            drawFrame("", "Watch!");
            StdDraw.pause(500);
        }
        drawFrame("", "Type!");
    }

    public String solicitNCharsInput(int n) {
        String typed = "";
        int idx = 0;
        while (idx < n) {
            if (StdDraw.hasNextKeyTyped()) {
                typed += StdDraw.nextKeyTyped();
                drawFrame(typed, "Type!");
                idx += 1;
            }
        }
        StdDraw.pause(1000);

        return typed;
    }

    public void startGame() {
        round = 1;

        while (!gameOver) {
            drawFrame("Round: " + round, "Watch!");
            StdDraw.pause(1000);
            String target = generateRandomString(round);
            flashSequence(target);
            String typed = solicitNCharsInput(round);
            if (!typed.equals(target)) {
                gameOver = true;
            }
            round += 1;
        }
        drawFrame("Game Over! You made it to round: " + (round - 1), "Game Over!");
    }

}
