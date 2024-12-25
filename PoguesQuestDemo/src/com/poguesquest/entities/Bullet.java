package com.poguesquest.entities;

import com.poguesquest.utils.Collider;

import java.awt.*;
import java.util.List;

public class Bullet {
    private int x, y;
    private int speed = 8;
    private int directionX, directionY;
    private static final int DAMAGE = 1;
    private boolean isAlive = true;
    private Collider collider;

    public Bullet(int startX, int startY, double firingAngle) {
        this.x = startX;
        this.y = startY;

        this.directionX = (int) (Math.cos(firingAngle) * speed);
        this.directionY = (int) (Math.sin(firingAngle) * speed);
        this.collider = new Collider(x, y, 5, 5);
    }

    public void update(int[][] map, int tileSize, List<Entity> entities) {
        x += directionX;
        y += directionY;

        collider.setPosition(x, y);

        if (collider.isColliding(map, tileSize)) {
            isAlive = false;
        }

        if (isAlive) {
            for (Entity entity : entities) {
                if (collider.isColliding(entity.getHitbox())) {
                    entity.damage(DAMAGE);
                    isAlive = false;
                    break;
                }
            }
        }
    }

    public void render(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(x, y, 10, 10);
    }

    public boolean isAlive() {
        return isAlive;
    }

    public Rectangle getHitbox() {
        return collider.getHitbox();
    }

    public int getDamage() {
        return DAMAGE;
    }
}