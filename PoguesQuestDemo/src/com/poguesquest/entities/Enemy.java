package com.poguesquest.entities;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Enemy extends Entity {
    protected int health;
    protected BufferedImage sprite;

    public Enemy(int x, int y, int width, int height, int health, BufferedImage sprite) {
        super(x, y, width, height);
        this.health = health;
        this.sprite = sprite;
    }

    // Getter and setter methods for health
    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    // Getter and setter methods for position
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
        updateHitbox();
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
        updateHitbox();
    }

    // Getter and setter methods for sprite
    public BufferedImage getSprite() {
        return sprite;
    }

    public void setSprite(BufferedImage sprite) {
        this.sprite = sprite;
    }

    @Override
    public void render(Graphics g) {
        g.drawImage(sprite, x, y, null);
    }

    @Override
    public abstract void update();
}