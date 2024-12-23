package com.poguesquest.entities;

import java.awt.*;

public abstract class Entity {
    protected int x, y;
    protected int width, height;
    protected Rectangle hitbox;

    public Entity(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hitbox = new Rectangle(x, y, width, height);
    }

    // Update the hitbox position
    protected void updateHitbox() {
        hitbox.setLocation(x, y);
    }

    // Check for collision with another entity
    public boolean collidesWith(Entity other) {
        return hitbox.intersects(other.hitbox);
    }

    // Check for collision with a rectangle (e.g., a wall)
    public boolean collidesWith(Rectangle rect) {
        return hitbox.intersects(rect);
    }

    // Abstract methods for rendering and updating
    public abstract void render(Graphics g);
    public abstract void update();
}