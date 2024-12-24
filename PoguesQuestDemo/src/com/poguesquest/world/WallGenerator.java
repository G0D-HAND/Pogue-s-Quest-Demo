package com.poguesquest.world;

public class WallGenerator {
    private final int[][] map;
    private final int width;
    private final int height;

    // Constructor to initialize the wall generator with the map dimensions and map grid
    public WallGenerator(int width, int height, int[][] map) {
        this.width = width;
        this.height = height;
        this.map = map;
    }

    // Method to initialize the map with walls
    public void generateWalls() {
        // Set all tiles to walls (0 = wall)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                map[y][x] = 0;
            }
        }
    }

    // Method to update wall types based on neighboring tiles
    public void updateWallTypes() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (map[y][x] == 0) { // If the current tile is a wall
                    int binaryRepresentation = 0;

                    // Check the four cardinal directions for floor tiles and set bits accordingly
                    if (y > 0 && map[y - 1][x] == 1) binaryRepresentation |= 1; // Top neighbor
                    if (y < height - 1 && map[y + 1][x] == 1) binaryRepresentation |= 2; // Bottom neighbor
                    if (x > 0 && map[y][x - 1] == 1) binaryRepresentation |= 4; // Left neighbor
                    if (x < width - 1 && map[y][x + 1] == 1) binaryRepresentation |= 8; // Right neighbor

                    // Update the map with the correct wall type based on binary representation
                    map[y][x] = getWallTile(binaryRepresentation);
                }
            }
        }
    }

    // Translate binary representation to tileset index
    private int getWallTile(int binaryRepresentation) {
        switch (binaryRepresentation) {
            case 0b0000: return 0;  // Isolated wall
            case 0b0001: return 1;  // Wall with floor above
            case 0b0010: return 2;  // Wall with floor below
            case 0b0011: return 3;  // Vertical wall (floor above and below)
            case 0b0100: return 4;  // Wall with floor to the left
            case 0b0101: return 5;  // Top-left corner
            case 0b0110: return 6;  // Bottom-left corner
            case 0b0111: return 7;  // Left-facing wall (floor on all left-side neighbors)
            case 0b1000: return 8;  // Wall with floor to the right
            case 0b1001: return 9;  // Top-right corner
            case 0b1010: return 10; // Bottom-right corner
            case 0b1011: return 11; // Right-facing wall (floor on all right-side neighbors)
            case 0b1100: return 12; // Horizontal wall (floor left and right)
            case 0b1101: return 13; // Top edge wall
            case 0b1110: return 14; // Bottom edge wall
            case 0b1111: return 15; // Fully surrounded (center)
            default: return 0;      // Default to isolated wall
        }
    }

    // Method to create a boundary wall around a 5x5 area centered on the player
    public void createBoundaryWalls(int startX, int startY) {
        for (int y = startY - 2; y <= startY + 2; y++) {
            for (int x = startX - 2; x <= startX + 2; x++) {
                if (x == startX - 2 || x == startX + 2 || y == startY - 2 || y == startY + 2) {
                    map[y][x] = 0; // Set boundary tiles to walls
                } else {
                    map[y][x] = 1; // Set inner tiles to floors
                }
            }
        }
    }

    // Method to create a random hole in one of the sides of the 5x5 area
    public void createRandomHole(int startX, int startY) {
        int side = (int) (Math.random() * 4); // Randomly select a side (0 = top, 1 = bottom, 2 = left, 3 = right)
        int holePosition;

        switch (side) {
            case 0: // Top side (y = startY - 2)
                holePosition = startX + (int) (Math.random() * 3) - 1;
                map[startY - 2][holePosition] = 1; // Create hole in the top boundary wall
                break;
            case 1: // Bottom side (y = startY + 2)
                holePosition = startX + (int) (Math.random() * 3) - 1;
                map[startY + 2][holePosition] = 1; // Create hole in the bottom boundary wall
                break;
            case 2: // Left side (x = startX - 2)
                holePosition = startY + (int) (Math.random() * 3) - 1;
                map[holePosition][startX - 2] = 1; // Create hole in the left boundary wall
                break;
            case 3: // Right side (x = startX + 2)
                holePosition = startY + (int) (Math.random() * 3) - 1;
                map[holePosition][startX + 2] = 1; // Create hole in the right boundary wall
                break;
        }
    }

    // Helper method to check if a tile is a wall
    public boolean isWall(int tile) {
        return tile == 0;
    }
}
