package com.poguesquest.utils;

import java.util.Random;

public class MapGenerator {
    private final int width;  // Total width of the map (in tiles)
    private final int height; // Total height of the map (in tiles)
    private final int[][] map; // The map grid (1 = floor, 0 = wall, 2 = floor below wall, 3 = floor above wall, 4 = wall corner, 5 = wall without floor beside it, 6 = wall beside floor on left, 7 = wall beside floor on right, 8 = pillar)
    private final Random random;
    private final int boundaryOffset = 1; // Ensure a wall on the sides

    // Constructor to initialize the map dimensions and random seed
    public MapGenerator(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        this.map = new int[height][width];
        this.random = new Random(seed);
    }

    // Main method to generate the map using a random walker algorithm
    public int[][] generateMap(int steps, int startX, int startY) {
        // Initialize map with walls (0 = wall)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                map[y][x] = 0;
            }
        }

        // Ensure the starting position is within bounds
        if (startX < boundaryOffset || startX >= width - boundaryOffset || startY < boundaryOffset || startY >= height - boundaryOffset) {
            throw new IllegalArgumentException("Starting position is out of bounds");
        }

        // Carve a 3x3 area around the starting position
        carve(startX, startY, 1);

        // Start the walkers at the specified positions within the 3x3 space
        int[][] walkerPositions = {{startX, startY - 1}, {startX - 1, startY}, {startX + 1, startY}, {startX, startY + 1}};
        for (int[] position : walkerPositions) {
            int currentX = position[0];
            int currentY = position[1];

            // Perform random walk
            for (int i = 0; i < steps; i++) {
                // Chance to place a larger room
                if (random.nextInt(15) == 0) {
                    int roomWidth = random.nextInt(4) + 4; // Random room width between 4 and 7
                    int roomHeight = random.nextInt(4) + 4; // Random room height between 4 and 7
                    placeRoom(currentX, currentY, roomWidth, roomHeight); // Place a random sized room
                    placeCoverDots(currentX, currentY, roomWidth, roomHeight, 3); // Add cover dots
                }

                // Choose a random direction
                int direction = random.nextInt(4);
                int stepSize = random.nextInt(3) + 1; // Random step size (1-3 tiles)

                switch (direction) {
                    case 0: // Up
                        currentY = Math.max(boundaryOffset, currentY - stepSize);
                        break;
                    case 1: // Down
                        currentY = Math.min(height - 1 - boundaryOffset, currentY + stepSize);
                        break;
                    case 2: // Left
                        currentX = Math.max(boundaryOffset, currentX - stepSize);
                        break;
                    case 3: // Right
                        currentX = Math.min(width - 1 - boundaryOffset, currentX + stepSize);
                        break;
                }

                // Carve a wider path
                carve(currentX, currentY, 1);
            }
        }

        // Create a wall on the outer edges of a 5x5 area around the player
        for (int y = startY - 2; y <= startY + 2; y++) {
            for (int x = startX - 2; x <= startX + 2; x++) {
                if (x == startX - 2 || x == startX + 2 || y == startY - 2 || y == startY + 2) {
                    map[y][x] = 0; // Wall
                } else {
                    map[y][x] = 1; // Floor
                }
            }
        }

        // Create a random hole on one of the sides (never the corners) of the 5x5 area
        createRandomHole(startX, startY);

        // Update wall types
        updateWallTypes();

        // Update floor tiles below and above walls
        updateFloorsAroundWalls();

        return map;
    }

    // Carve a square area of the given size centered at (centerX, centerY)
    private void carve(int centerX, int centerY, int size) {
        for (int y = centerY - size; y <= centerY + size; y++) {
            for (int x = centerX - size; x <= centerX + size; x++) {
                if (x >= boundaryOffset && x < width - boundaryOffset && y >= boundaryOffset && y < height - boundaryOffset) {
                    map[y][x] = 1; // Carve floor
                }
            }
        }
    }

    // Place a rectangular room centered at (centerX, centerY) with specified width and height
    private void placeRoom(int centerX, int centerY, int roomWidth, int roomHeight) {
        for (int y = centerY - roomHeight / 2; y <= centerY + roomHeight / 2; y++) {
            for (int x = centerX - roomWidth / 2; x <= centerX + roomWidth / 2; x++) {
                if (x >= boundaryOffset && x < width - boundaryOffset && y >= boundaryOffset && y < height - boundaryOffset) {
                    map[y][x] = 1; // Carve floor
                }
            }
        }
    }

    // Place random cover dots in a room for hiding spots
    private void placeCoverDots(int centerX, int centerY, int roomWidth, int roomHeight, int dotCount) {
        for (int i = 0; i < dotCount; i++) {
            int dotX = centerX + random.nextInt(roomWidth) - roomWidth / 2;
            int dotY = centerY + random.nextInt(roomHeight) - roomHeight / 2;

            if (dotX >= boundaryOffset && dotX < width - boundaryOffset && dotY >= boundaryOffset && dotY < height - boundaryOffset) {
                map[dotY][dotX] = 0; // Place a wall as cover dot
            }
        }
    }

    // Create a random hole on one of the sides (never the corners) of the 5x5 area
    private void createRandomHole(int startX, int startY) {
        int side = random.nextInt(4); // Randomly select a side (0 = top, 1 = bottom, 2 = left, 3 = right)
        int holePosition;

        switch (side) {
            case 0: // Top side (y = startY - 2)
                holePosition = startX + random.nextInt(3) - 1;
                map[startY - 2][holePosition] = 1;
                break;
            case 1: // Bottom side (y = startY + 2)
                holePosition = startX + random.nextInt(3) - 1;
                map[startY + 2][holePosition] = 1;
                break;
            case 2: // Left side (x = startX - 2)
                holePosition = startY + random.nextInt(3) - 1;
                map[holePosition][startX - 2] = 1;
                break;
            case 3: // Right side (x = startX + 2)
                holePosition = startY + random.nextInt(3) - 1;
                map[holePosition][startX + 2] = 1;
                break;
        }
    }

 // Update wall types based on surrounding tiles
    private void updateWallTypes() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (map[y][x] == 0) { // Wall
                    boolean hasFloorAbove = (y > 0 && map[y - 1][x] == 1);
                    boolean hasFloorBelow = (y < height - 1 && map[y + 1][x] == 1);
                    boolean hasFloorLeft = (x > 0 && map[y][x - 1] == 1);
                    boolean hasFloorRight = (x < width - 1 && map[y][x + 1] == 1);
                    boolean hasWallAbove = (y > 0 && isWall(map[y - 1][x]));
                    boolean hasWallBelow = (y < height - 1 && isWall(map[y + 1][x]));
                    boolean hasWallLeft = (x > 0 && isWall(map[y][x - 1]));
                    boolean hasWallRight = (x < width - 1 && isWall(map[y][x + 1]));

                    if (!hasWallAbove && !hasWallBelow && !hasWallLeft && !hasWallRight) {
                        map[y][x] = 8; // Pillar
                    } else if (hasFloorAbove && hasFloorBelow && hasFloorLeft && hasFloorRight) {
                        map[y][x] = 4; // Wall corner
                    } else if (!hasFloorAbove && !hasFloorBelow && !hasFloorLeft && !hasFloorRight) {
                        map[y][x] = 5; // Wall without floor beside it
                    } else if (hasFloorLeft && !hasFloorRight) {
                        map[y][x] = 6; // Wall beside floor on left
                    } else if (!hasFloorLeft && hasFloorRight) {
                        map[y][x] = 7; // Wall beside floor on right
                    } else if (hasWallAbove && hasWallBelow && !hasWallLeft && !hasWallRight) {
                        map[y][x] = 9; // Straight wall
                    } else if (!hasWallAbove && hasWallBelow && hasWallLeft && hasWallRight) {
                        map[y][x] = 10; // Cap wall
                    }
                }
            }
        }
    }



    // Update floor tiles below and above walls
    private void updateFloorsAroundWalls() {
        for (int y = 0; y < height - 1; y++) {
            for (int x = 0; x < width; x++) {
                // Check if there's a wall above a floor
                if (isWall(map[y][x]) && map[y + 1][x] == 1) {
                    map[y + 1][x] = 2; // Mark floor below wall
                }
                // Check if there's a floor above a wall
                if (map[y][x] == 1 && isWall(map[y + 1][x])) {
                    map[y][x] = 3; // Mark floor above wall
                }
            }
        }
    }

    // Helper method to check if a tile is a wall
    private boolean isWall(int tile) {
        return tile == 0 || tile == 4 || tile == 5 || tile == 6 || tile == 7 || tile == 8;
    }

    // Debug method to print the map to the console
    public void printMap() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                switch (map[y][x]) {
                    case 1:
                        System.out.print("."); // Floor 1
                        break;
                    case 0:
                        System.out.print("#"); // Wall 0
                        break;
                    case 2:
                        System.out.print("+"); // Floor below wall 2
                        break;
                    case 3:
                        System.out.print("-"); // Floor above wall 3
                        break;
                    case 4:
                        System.out.print("*"); // Wall corner 4
                        break;
                    case 5:
                        System.out.print("o"); // Wall without floor beside it 5
                        break;
                    case 6:
                        System.out.print("<"); // Wall beside floor on left 6
                        break;
                    case 7:
                        System.out.print(">"); // Wall beside floor on right 7
                        break;
                    case 8:
                        System.out.print("|"); // Pillar 8
                        break;
                }
            }
            System.out.println();
        }
    }

    // Getters
    public int[][] getMap() {
        return map;
    }
}
