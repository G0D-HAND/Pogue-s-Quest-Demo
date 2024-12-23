package com.poguesquest.world;

import java.util.Random;

public class MapGenerator {
    private final int width;
    private final int height;
    private final int[][] map;
    private final Random random;
    private final WallGenerator wallGenerator;
    private final FloorGenerator floorGenerator;

    // Constructor to initialize the map generator with dimensions and seed
    public MapGenerator(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        this.map = new int[height][width];
        this.random = new Random(seed);
        this.wallGenerator = new WallGenerator(width, height, map);
        this.floorGenerator = new FloorGenerator(width, height, map);
    }

    // Method to generate the map with specified steps and starting position
    public int[][] generateMap(int steps, int startX, int startY) {
        wallGenerator.generateWalls();

        // Ensure the starting position is within bounds
        if (startX < 1 || startX >= width - 1 || startY < 1 || startY >= height - 1) {
            throw new IllegalArgumentException("Starting position is out of bounds");
        }

        // Carve the initial floor area
        floorGenerator.carve(startX, startY, 1);

        // Define initial walker positions
        int[][] walkerPositions = {{startX, startY - 1}, {startX - 1, startY}, {startX + 1, startY}, {startX, startY + 1}};
        for (int[] position : walkerPositions) {
            int currentX = position[0];
            int currentY = position[1];

            // Perform random walk to generate the map
            for (int i = 0; i < steps; i++) {
                // Occasionally place a room
                if (random.nextInt(15) == 0) {
                    int roomWidth = random.nextInt(4) + 4;
                    int roomHeight = random.nextInt(4) + 4;
                    floorGenerator.placeRoom(currentX, currentY, roomWidth, roomHeight);
                    floorGenerator.placeCoverDots(currentX, currentY, roomWidth, roomHeight, 3);
                }

                // Randomly choose a direction to walk
                int direction = random.nextInt(4);
                int stepSize = random.nextInt(3) + 1;

                switch (direction) {
                    case 0: // Up
                        currentY = Math.max(1, currentY - stepSize);
                        break;
                    case 1: // Down
                        currentY = Math.min(height - 1 - 1, currentY + stepSize);
                        break;
                    case 2: // Left
                        currentX = Math.max(1, currentX - stepSize);
                        break;
                    case 3: // Right
                        currentX = Math.min(width - 1 - 1, currentX + stepSize);
                        break;
                }

                // Carve a floor at the new position
                floorGenerator.carve(currentX, currentY, 1);
            }
        }

        // Create boundaries and random hole
        wallGenerator.createBoundaryWalls(startX, startY);
        wallGenerator.createRandomHole(startX, startY);

        // Update wall types and floor types
        wallGenerator.updateWallTypes();
        floorGenerator.updateFloorsAroundWalls();
        floorGenerator.updateFloorTypes();

        return map;
    }

    // Method to print the map for debugging
    public void printMap() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                switch (map[y][x]) {
                    case 1:
                        System.out.print("."); // Floor
                        break;
                    case 0:
                        System.out.print("#"); // Wall
                        break;
                    case 2:
                        System.out.print("+"); // Floor below wall
                        break;
                    case 3:
                        System.out.print("-"); // Floor above wall
                        break;
                    default:
                        System.out.print(Integer.toHexString(map[y][x])); // Print binary representation for walls
                        break;
                }
            }
            System.out.println();
        }
    }

    // Getter for the map
    public int[][] getMap() {
        return map;
    }
}