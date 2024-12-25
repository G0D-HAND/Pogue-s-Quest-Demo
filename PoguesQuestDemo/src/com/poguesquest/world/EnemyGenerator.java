package com.poguesquest.world;

import com.poguesquest.entities.Guardian;
import com.poguesquest.entities.Player;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnemyGenerator {
    private final int[][] map;
    private final int mapWidth;
    private final int mapHeight;
    private final Random random;
    private final Player player;
    private final BufferedImage walkingSpriteSheet;
    private final BufferedImage idleSpriteSheet;
    private static final int WORLD_WIDTH = 1024;
    private static final int WORLD_HEIGHT = 1024;
    private static final int MIN_DISTANCE = 64; // Minimum distance between enemies

    public EnemyGenerator(int[][] map, int mapWidth, int mapHeight, long seed, Player player, BufferedImage walkingSpriteSheet, BufferedImage idleSpriteSheet) {
        this.map = map;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.random = new Random(seed);
        this.player = player;
        this.walkingSpriteSheet = walkingSpriteSheet;
        this.idleSpriteSheet = idleSpriteSheet;
    }

    public List<Guardian> generateEnemies(int minEnemies) {
        List<Guardian> enemies = new ArrayList<>();

        int numEnemies = random.nextInt(10) + minEnemies; // Ensure at least minEnemies
        System.out.println("Generating " + numEnemies + " enemies.");
        
        for (int i = 0; i < numEnemies; i++) {
            int x, y;
            boolean validLocation;
            do {
                x = random.nextInt(WORLD_WIDTH / 32); // Ensure within world boundaries
                y = random.nextInt(WORLD_HEIGHT / 32); // Ensure within world boundaries
                validLocation = isValidSpawnLocation(x, y, enemies);
            } while (!validLocation);

            System.out.println("Spawning enemy at (" + x * 32 + ", " + y * 32 + ")");
            enemies.add(new Guardian(x * 32, y * 32, 32, 32, walkingSpriteSheet, idleSpriteSheet, player, map, null));
        }

        return enemies;
    }

    private boolean isValidSpawnLocation(int x, int y, List<Guardian> enemies) {
        // Ensure location is on the floor and not within a 1x1 area around the player
        if (map[y][x] != 1) return false;

        int playerTileX = player.getX() / 32;
        int playerTileY = player.getY() / 32;

        // Ensure the spawn location is not within a 1x1 area around the player
        if (Math.abs(x - playerTileX) <= 1 && Math.abs(y - playerTileY) <= 1) return false;

        // Ensure the spawn location is not too close to other enemies
        for (Guardian enemy : enemies) {
            int enemyTileX = enemy.getX() / 32;
            int enemyTileY = enemy.getY() / 32;
            if (Math.abs(x - enemyTileX) * 32 < MIN_DISTANCE && Math.abs(y - enemyTileY) * 32 < MIN_DISTANCE) {
                return false;
            }
        }

        return true;
    }
}
