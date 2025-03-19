package byow.Core;

import byow.Input.InputSource;
import byow.Input.StringInputSource;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.*;

/**
 * A Util class for tile engine.
 */
public class Utils {

    /**
     * The curren working directory.
     */
    static final File CWD = new File(System.getProperty("user.dir"));
    static final File SAVEFILE = new File(CWD, "save.txt");

    /**
     * Get the avatar's coordinate(x or y).
     *
     * @param w the world where the avatar is
     * @param isX if it's to get X's coordinate, true for X and false for Y
     * @return the coordinate correspond to x or y
     */
    static int avatarCoordinate(World w, boolean isX) {
        int width = w.getWidth();
        int height = w.getHeight();
        TETile[][] tiles = w.getTiles();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (tiles[x][y].equals(Tileset.AVATAR)) {
                    return isX ? x : y;
                }
            }
        }
        return -1;
    }

    /**
     * Get the avatar's X coordinate.
     *
     * @param w the world where the avatar is
     * @return the X coordinate of avatar
     */
    static int avatarX(World w) {
        return avatarCoordinate(w, true);
    }

    /**
     * Get the avatar's Y coordinate
     *
     * @param w the world where the avatar is
     * @return the Y coordinate of avatar
     */
    static int avatarY(World w) {
        return avatarCoordinate(w, false);
    }

    /**
     * Move the tile of {@code {x, y}} to {@code {x + xOffset, y + yOffset}},
     * and then set {@code {x, y}} to {@code FLOOR}.
     *
     * @param tiles the tiles where to execute move
     * @param x the x coordinate of source
     * @param y the y coordinate of source
     * @param xOffset the offset of x
     * @param yOffset the offset of y
     */
    static void move(TETile[][] tiles, int x, int y, int xOffset, int yOffset) {
        if (tiles[x + xOffset][y + yOffset] == Tileset.FLOOR) {
            tiles[x][y] = Tileset.FLOOR;
            tiles[x + xOffset][y + yOffset] = Tileset.AVATAR;
        }
    }

    /**
     * Move the avatar to the {@code dir}, and set the original position
     * to {@code FLOOR}.
     *
     * @param w the world where the avatar is
     * @param dir the direction to move, basically W, S, A and D
     */
    static void move(World w, char dir) {
        int width = w.getWidth();
        int height = w.getHeight();
        int avatarX = avatarX(w);
        int avatarY = avatarY(w);
        TETile[][] tiles = w.getTiles();
        switch (dir) {
            case 'W':
                if (avatarY + 1 < height) {
                    move(tiles, avatarX, avatarY, 0, 1);
                }
                break;
            case 'S':
                if (avatarY - 1 >= 0) {
                    move(tiles, avatarX, avatarY, 0, -1);
                }
                break;
            case 'A':
                if (avatarX - 1 >= 0) {
                    move(tiles, avatarX, avatarY, -1, 0);
                }
                break;
            case 'D':
                if (avatarX + 1 < width) {
                    move(tiles, avatarX, avatarY, 1, 0);
                }
                break;
            default:
                System.err.println("Invalid dir: " + dir);
                break;
        }
    }

    /**
     * Save the game state and quit.
     *
     * @param input the input to generate world
     */
    static void save(String input) {
        try (FileWriter writer = new FileWriter(SAVEFILE)) {
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                writer.write(c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the world which is the same as the last quit.
     *
     * @return the world same as last quit
     */
    static World load() {
        String state = loadState().toString();
        StringInputSource source = new StringInputSource(state);
        return generateWithString(source);
    }

    /**
     * Load the state exact as the last quit.
     *
     * @return the state which type is {@code StringBuilder}
     */
    static StringBuilder loadState() {
        StringBuilder s = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(SAVEFILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                s.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }


    /**
     * Generate world with the input source.
     *
     * @param source the input source.
     * @return the world correspond to the input source
     */
    static World generateWithString(StringInputSource source) {
        World world = null;
        StringBuilder state = new StringBuilder();

        while (source.hasNextKey()) {
            char c = source.getNextKey();
            if (c == 'N') {
                StringBuilder s = new StringBuilder();
                state.append(c);
                while (source.hasNextKey()) {
                    char nc = source.getNextKey();
                    if (nc == 'S') {
                        state.append(nc);
                        break;
                    }
                    s.append(nc);
                    state.append(nc);
                }
                long seed = Long.parseLong(s.toString());
                world = new World(seed, 60, 40);
                world.generateWorld();
            } else if (c == ':') {
                source.getNextKey();
                save(state.toString());
            } else if (c == 'L') {
                world = load();
                state = loadState();
            } else {
                move(world, c);
                state.append(c);
            }
        }

        return world;
    }

}
