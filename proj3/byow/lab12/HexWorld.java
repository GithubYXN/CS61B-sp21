package byow.lab12;

import static byow.TileEngine.Tileset.NOTHING;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import java.util.Date;
import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int MAX_COLUMNS = 5;
    private static final int MIN_COLUMNS = 3;
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    private static final long seed = new Date().hashCode();
    private static final Random rand = new Random(seed);

    /** Picks a RANDOM tile with a 33% change of being
     *  a wall, 33% chance of being a flower, and 33%
     *  chance of being empty space.
     */
    private static TETile randomTile() {
        int tileNum = rand.nextInt(4);
        switch (tileNum) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.WATER;
            case 3: return Tileset.GRASS;
            default: return Tileset.NOTHING;
        }
    }

    /**
     * Fills the given 2D array of tiles with NOTHING tiles.
     * @param tiles
     */
    public static void fillWithNothingTiles(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = NOTHING;
            }
        }
    }

    /**
     * Add a single hexagon to the tile.
     *
     * @param c The start column.
     * @param r The start row.
     * @param tile The tile to draw.
     * @param size The hexagon's size.
     */
    public static void addHexagon(int c, int r, TETile[][] tile, int size) {
        TETile t = randomTile();
        for (int h = 0; h < size; h++) {
            for (int x = c - h; x < c + size + h; x++) {
                tile[x][h + r] = t;
                tile[x][r + 2 * size - 1 - h] = t;
            }
        }
    }

    /**
     * Add a columns of nums hexagons to tile.
     *
     * @param tile
     * @param size
     * @param nums
     */
    public static void addColumnHexagon(TETile[][] tile, int size, int nums) {
        int r = (HEIGHT - (MAX_COLUMNS * size * 2)) / 2 + ((MAX_COLUMNS - nums) * size);
        int c = WIDTH / 2 - size / 2 - (MAX_COLUMNS - nums) * (2 * size - 1);
        for (int n = 0; n < nums; n++) {
            addHexagon(c, r + 2 * size * n, tile, size);
            if (nums < MAX_COLUMNS) {
                addHexagon(c + (MAX_COLUMNS - nums) * (4 * size - 2), r + 2 * size * n, tile, size);
            }
        }
    }

    /**
     * Tessellate the columns to an entire world.
     *
     * @param tiles
     * @param size
     */
    public static void tessellate(TETile[][] tiles, int size) {
        for (int i = MAX_COLUMNS; i >= MIN_COLUMNS; i -= 1) {
            addColumnHexagon(tiles, size, i);
        }
    }


    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] tiles = new TETile[WIDTH][HEIGHT];
        fillWithNothingTiles(tiles);
        tessellate(tiles, 3);

        ter.renderFrame(tiles);
    }
}
