package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static byow.Core.RandomUtils.*;

public class World {

    private Random rand;
    private int height;
    private int width;
    private TETile[][] tiles;
    private List<Room> rooms;

    public World(Random rand, int width, int height) {
        this.rand = rand;
        this.height = height;
        this.width = width;
        this.tiles = new TETile[width][height];
        this.rooms = new ArrayList<>();
    }

    /**
     * An inner class represents a room object, with the value of its
     * left bottom, width and height.
     */
    private class Room {
        int leftBottomX;
        int leftBottomY;
        int height;
        int width;

        Room(int leftBottomX, int leftBottomY, int width, int height) {
            this.leftBottomX = leftBottomX;
            this.leftBottomY = leftBottomY;
            this.width = width;
            this.height = height;
        }
    }

    /**
     * Initial the world with tile of {@code NOTHING}
     */
    private void init() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    /**
     * Create a single room irrespective of whether it's valid
     * with random left bottom,width and height.
     *
     * @return a room object
     */
    private Room createRoom() {
        int lbx = uniform(rand, 1, width - 1);
        int lby = uniform(rand, 1, height - 1);
        int w = uniform(rand, 2, 6);
        int h = uniform(rand, 2, 6);

        return new Room(lbx, lby, w, h);
    }

    /**
     * Fill the room with tile of {@code FLOOR}
     *
     * @param room the room to fill with
     */
    private void fillRoom(Room room) {
        for (int x = room.leftBottomX; x < room.leftBottomX + room.width; x++) {
            for (int y = room.leftBottomY; y < room.leftBottomY + room.height; y++) {
                tiles[x][y] = Tileset.FLOOR;
            }
        }
    }

    /**
     * Create {@code num} valid rooms, and add them to {@code List<Room> rooms}.
     *
     * @param num the number of rooms
     */
    private void createRooms(int num) {
        for (int i = 0; i < num; i++) {
            Room room = createRoom();
            while (!isValidRoom(room)) {
                room = createRoom();
            }
            fillRoom(room);
            rooms.add(room);
        }
    }

    /**
     * Check if the given room is a valid room, a valid room is a room that not
     * exceed the tile and not overlap with other room.
     *
     * @param room the room to check out
     * @return true if it's valid and false if it isn't
     */
    private boolean isValidRoom(Room room) {
        if (room.leftBottomX + room.width > width - 1 || room.leftBottomY + room.height > height - 1) {
            return false;
        } else {
            for (int x = room.leftBottomX; x < room.leftBottomX + room.width; x++) {
                for (int y = room.leftBottomY; y < room.leftBottomY + room.height; y++) {
                    if (tiles[x][y] == Tileset.FLOOR) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    /**
     * Connect each room with hallway, the two endpoints are random points
     * in the two rooms.
     */
    private void connect() {
        while (rooms.size() > 1) {
            Room r1 = rooms.get(0);
            Room r2 = rooms.get(1);
            int r1X = r1.leftBottomX + uniform(rand, 0, r1.width);
            int r1Y = r1.leftBottomY + uniform(rand, 0, r1.height);
            int r2X = r2.leftBottomX + uniform(rand, 0, r2.width);
            int r2Y = r2.leftBottomY + uniform(rand, 0, r2.height);

            int stepX = r1X < r2X ? 1 : -1;
            int stepY = r1Y < r2Y ? 1 : -1;

            while (r1X != r2X && r1Y != r2Y) {
                boolean moveX = bernoulli(rand);
                if (moveX) {
                    r1X += stepX;
                } else {
                    r1Y += stepY;
                }
                tiles[r1X][r1Y] = Tileset.FLOOR;
            }
            while (r1X != r2X) {
                r1X += stepX;
                tiles[r1X][r1Y] = Tileset.FLOOR;
            }
            while (r1Y != r2Y) {
                r1Y += stepY;
                tiles[r1X][r1Y] = Tileset.FLOOR;
            }

            rooms.remove(r1);
        }
    }

    /**
     * Make all floors are surrounded with walls.
     */
    private void surroundWithWall() {
        int[][] dir = { {-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, 1}, {-1, -1}, {1, 1}, {1, -1} };
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (tiles[x][y] == Tileset.FLOOR) {
                    for (int k = 0; k < dir.length; k++) {
                        int newX = x + dir[k][0];
                        int newY = y + dir[k][1];
                        if (tiles[newX][newY] == Tileset.NOTHING) {
                            tiles[newX][newY] = Tileset.WALL;
                        }
                    }
                }
            }
        }
    }

    /**
     * Create an avatar in a random position.
     */
    private void createAvatar() {
        int x, y;
        do {
            x = uniform(rand, 1, width - 1);
            y = uniform(rand, 1, height - 1);
        } while (tiles[x][y] != Tileset.FLOOR);
        tiles[x][y] = Tileset.AVATAR;
    }

    /**
     * Generate the world with rooms of number {@code numOfRooms}, then connect them
     * and surround every floor with wall.
     *
     * @param numOfRooms the number of rooms to be generated
     */
    public void generateWorld() {
        int numOfRooms = uniform(rand, (width + height) / 6, (width + height) / 3);
        init();
        createRooms(numOfRooms);
        connect();
        surroundWithWall();
        createAvatar();
    }


    // Getter functions
    public TETile[][] getTiles() {
        return tiles;
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(60, 40);
        World world = new World(new Random(114514), 60, 40);
        world.generateWorld();
        ter.renderFrame(world.getTiles());
    }

}