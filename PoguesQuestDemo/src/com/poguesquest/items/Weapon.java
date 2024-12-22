package com.poguesquest.items;

import java.awt.*;
import java.awt.image.BufferedImage;
import com.poguesquest.entities.Player;

public abstract class Weapon {
    protected String name;    // Weapon name
    protected BufferedImage sprite; // Weapon sprite (image)
    protected int damage;    // Weapon damage

    // Constructor for Weapon class
    public Weapon(String name, BufferedImage sprite, int damage) {
        this.name = name;
        this.sprite = sprite;
        this.damage = damage;
    }

    // Abstract render method - every weapon must implement it
    public abstract void render(Graphics g, int playerX, int playerY, boolean facingRight);

    // Abstract attack method - to be implemented by subclasses (e.g., Gun, Melee)
    public abstract void attack(Player player);

    // Getter for name
    public String getName() {
        return name;
    }

    // Getter for sprite
    public BufferedImage getSprite() {
        return sprite;
    }

    // Getter for damage
    public int getDamage() {
        return damage;
    }
}