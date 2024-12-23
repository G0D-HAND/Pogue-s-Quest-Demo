package com.poguesquest.utils;

import java.awt.Rectangle;

public class Collider {
    private Rectangle hitbox;

    public Collider(int x, int y, int width, int height) {
        this.hitbox = new Rectangle(x, y, width, height);
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public void setPosition(int x, int y) {
        hitbox.setLocation(x, y);
    }

    public boolean isColliding(Collider other) {
        return hitbox.intersects(other.getHitbox());
    }

    public boolean isColliding(int[][] map, int tileSize) {
        int startX = Math.max(0, hitbox.x / tileSize);
        int startY = Math.max(0, hitbox.y / tileSize);
        int endX = Math.min(map[0].length - 1, (hitbox.x + hitbox.width) / tileSize);
        int endY = Math.min(map.length - 1, (hitbox.y + hitbox.height) / tileSize);

        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                if (map[y][x] == 0 ||
                	map[y][x] == 4 ||
                	map[y][x] == 5 ||
                	map[y][x] == 6 ||
                	map[y][x] == 7 ||
                	map[y][x] == 8) { // Assuming 0 is wall
                    return true;
                }
            }
        }
        return false;
    }
}