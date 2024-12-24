package com.poguesquest.world;

public class FloorGenerator {
    private final int[][] map;
    private final int width;
    private final int height;
    private final WallGenerator wallGenerator;

    // Constructor to initialize the floor generator with the map dimensions and map grid
    public FloorGenerator(int width, int height, int[][] map) {
        this.width = width;
        this.height = height;
        this.map = map;
        this.wallGenerator = new WallGenerator(width, height, map);
    }

    // Method to carve out a floor area
    public void carve(int centerX, int centerY, int size) {
        for (int y = centerY - size; y <= centerY + size; y++) {
            for (int x = centerX - size; x <= centerX + size; x++) {
                if (x >= 1 && x < width - 1 && y >= 1 && y < height - 1) {
                    map[y][x] = 1; // Carve floor
                }
            }
        }
    }

    // Method to place a rectangular room
    public void placeRoom(int centerX, int centerY, int roomWidth, int roomHeight) {
        for (int y = centerY - roomHeight / 2; y <= centerY + roomHeight / 2; y++) {
            for (int x = centerX - roomWidth / 2; x <= centerX + roomWidth / 2; x++) {
                if (x >= 1 && x < width - 1 && y >= 1 && y < height - 1) {
                    map[y][x] = 1; // Carve floor
                }
            }
        }
    }

    // Method to place cover dots (walls) within a room
    public void placeCoverDots(int centerX, int centerY, int roomWidth, int roomHeight, int dotCount) {
        for (int i = 0; i < dotCount; i++) {
            int dotX = centerX + (int) (Math.random() * roomWidth) - roomWidth / 2;
            int dotY = centerY + (int) (Math.random() * roomHeight) - roomHeight / 2;

            if (dotX >= 1 && dotX < width - 1 && dotY >= 1 && dotY < height - 1) {
                map[dotY][dotX] = 0; // Place a wall as cover dot
            }
        }
    }

    // Method to update floor types around walls
    public void updateFloorsAroundWalls() {
        for (int y = 0; y < height - 1; y++) {
            for (int x = 0; x < width; x++) {
                if (wallGenerator.isWall(map[y][x]) && map[y + 1][x] == 1) {
                    map[y + 1][x] = 2; // Mark floor below wall
                }
                if (map[y][x] == 1 && wallGenerator.isWall(map[y + 1][x])) {
                    map[y][x] = 3; // Mark floor above wall
                }
            }
        }
    }

    // Method to update floor types based on neighboring walls
    public void updateFloorTypes() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (map[y][x] == 1) { // Floor
                    int binaryRepresentation = 0;
                    // Check the four cardinal directions for wall tiles and set bits accordingly
                    if (y > 0 && wallGenerator.isWall(map[y - 1][x])) binaryRepresentation |= 1; // Top neighbor
                    if (y < height - 1 && wallGenerator.isWall(map[y + 1][x])) binaryRepresentation |= 2; // Bottom neighbor
                    if (x > 0 && wallGenerator.isWall(map[y][x - 1])) binaryRepresentation |= 4; // Left neighbor
                    if (x < width - 1 && wallGenerator.isWall(map[y][x + 1])) binaryRepresentation |= 8; // Right neighbor

                    map[y][x] = binaryRepresentation + 1; // Assign based on wall neighbors, adding 1 to avoid collision with wall value 0
                }
            }
        }
    }
}