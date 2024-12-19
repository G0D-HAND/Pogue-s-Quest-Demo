package com.poguesquest.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Bullet {
    private int x, y;
    private int speed = 10;
    private int directionX, directionY;
    private int damage;
    private boolean isAlive = true;

    public Bullet(int startX, int startY, int targetX, int targetY, int damage) {
        this.x = startX;
        this.y = startY;
        this.damage = damage;

        double angle = Math.atan2(targetY - startY, targetX - startX);
        this.directionX = (int) (Math.cos(angle) * speed);
        this.directionY = (int) (Math.sin(angle) * speed);
    }

    public void update() {
        x += directionX;
        y += directionY;

        // Remove bullet if it goes off-screen
        if (x < 0 || x > 800 || y < 0 || y > 600) {
            isAlive = false;
        }
    }

    public void render(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(x, y, 5, 5); // Simple bullet as a red circle
    }

    public boolean isAlive() {
        return isAlive;
    }

    public Rectangle getHitbox() {
        return new Rectangle(x, y, 5, 5);
    }

    public int getDamage() {
        return damage;
    }
}
