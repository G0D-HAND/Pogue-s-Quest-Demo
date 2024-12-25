package com.poguesquest.world;

import com.poguesquest.entities.Guardian;
import com.poguesquest.entities.Player;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnemyGenerator {
    private final int[][] map;
    private final int width;
    private final int height;
    private final Random random;
    private final Player player;
    private final BufferedImage walkingSpriteSheet;
    private final BufferedImage idleSpriteSheet;

    public EnemyGenerator(int[][] map, int width, int height, long seed, Player player, BufferedImage walkingSpriteSheet, BufferedImage idleSpriteSheet) {
        this.map = map;
        this.width = width;
        this.height = height;
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
            do {
                x = random.nextInt(width);
                y = random.nextInt(height);
            } while (!isValidSpawnLocation(x, y));

            System.out.println("Spawning enemy at (" + x + ", " + y + ")");
            enemies.add(new Guardian(x * 32, y * 32, 32, 32, walkingSpriteSheet, idleSpriteSheet, 300, player, map));
        }

        // Ensure separation between guardians
        for (Guardian guardian : enemies) {
            guardian.separateFromOthers(enemies);
        }

        return enemies;
    }

    private boolean isValidSpawnLocation(int x, int y) {
        // Ensure location is on the floor and not within a 5x5 area around the player
        if (map[y][x] != 1) return false;

        int playerTileX = player.getX() / 32;
        int playerTileY = player.getY() / 32;

        return Math.abs(x - playerTileX) > 2 && Math.abs(y - playerTileY) > 2;
    }
}