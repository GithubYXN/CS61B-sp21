package byow.Core;

import byow.Input.KeyBoardInputSource;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

import static byow.Core.Utils.*;

public class InteractGame {
    private int width;
    private int height;
    private TERenderer ter;
    private World world;
    private KeyBoardInputSource source;
    private String state;

    public InteractGame(int width, int height) {
        this.width = width;
        this.height = height;
        this.source = new KeyBoardInputSource();
        this.state = "N";
        this.world = null;
        this.ter = new TERenderer();
        ter.initialize(width, height);
    }

    public long readSeed(KeyBoardInputSource source) {
        String seed = "";
        while (source.hasNextKey()) {
            char c = source.getNextKey();
            if (c == 'S') {
                state += c;
                return Long.parseLong(seed);
            }
            state += c;
            seed += c;
        }
        return -1;
    }

    public void handleCommand(KeyBoardInputSource source) {
        while (source.hasNextKey()) {
            char c = source.getNextKey();
            if (c == ':') {
                source.getNextKey();
                save(state);
                startGame();
            } else {
                move(world, c);
                state += c;
                drawFrame(world.getTiles());
            }
        }
    }

    public void drawFrame(TETile[][] tiles) {
        StdDraw.clear();
        ter.renderFrame(tiles);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.textLeft(1, height - 1, mouseAt(world.getTiles()));
        StdDraw.show();
    }

    public String mouseAt(TETile[][] tiles) {
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return tiles[x][y].description();
        }
        return "out of bounds";
    }

    public void showMenu() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 40));
        StdDraw.text(width / 2, height * 4 / 5, "CS61B: THE WORLD");
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 20));
        StdDraw.text(width / 2, height / 2, "New Game (N)");
        StdDraw.text(width / 2, height / 2 - 5, "Load Game (L)");
        StdDraw.text(width / 2, height / 2 - 10, "Quit (Q)");

        StdDraw.setFont(new Font("Monaco", Font.BOLD, 14));
        StdDraw.show();
    }

    public void startGame() {
        showMenu();
        char command = source.getNextKey();
        if (command == 'N') {
            long seed = readSeed(source);
            world = new World(seed, width, height);
            world.generateWorld();
        } else if (command == 'L') {
            world = load();
            state = loadState().toString();
        } else if (command == 'Q') {
            System.exit(0);
        }
        drawFrame(world.getTiles());
        handleCommand(source);
    }

}
