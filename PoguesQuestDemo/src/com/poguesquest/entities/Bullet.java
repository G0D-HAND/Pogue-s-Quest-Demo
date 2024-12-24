package com.poguesquest.entities;

import com.poguesquest.utils.Collider;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Bullet {
    private int x, y;
    private int speed = 10;
    private int directionX, directionY;
    private int damage;
    private boolean isAlive = true;
    private Collider collider;

    public Bullet(int startX, int startY, double firingAngle, int damage) {
        this.x = startX;
        this.y = startY;
        this.damage = damage;

        this.directionX = (int) (Math.cos(firingAngle) * speed);
        this.directionY = (int) (Math.sin(firingAngle) * speed);
        this.collider = new Collider(x, y, 5, 5);
    }

    public void update(int[][] map, int tileSize) {
        x += directionX;
        y += directionY;

        collider.setPosition(x, y);

        if (collider.isColliding(map, tileSize)) {
            isAlive = false;
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
        return damage;
    }
}