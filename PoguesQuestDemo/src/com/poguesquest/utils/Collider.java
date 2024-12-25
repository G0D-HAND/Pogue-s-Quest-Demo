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
                if (isWall(map[y][x])) {
                    return true;
                }
            }
        }
        return false;
    }

    // Helper method to check if a tile is a wall
    private boolean isWall(int tile) {
        return tile > 1 && tile <= 15; // Assuming wall tiles range from 0 to 15
    }
}