package com.poguesquest.entities;

import java.awt.*;
import java.awt.image.BufferedImage;

import com.poguesquest.items.Gun;

public class Spirit extends Enemy {
    private Gun gun; // Assume Gun class is defined somewhere

    public Spirit(int x, int y, int width, int height, int health, BufferedImage sprite, Gun gun) {
        super(x, y, width, height, health, sprite);
        this.gun = gun;
    }

    @Override
    public void update() {
        // Implement ranged behavior here
        // Example: Keep distance from the player and shoot
        gun.updateCursorPosition(new Point(x, y)); // Update gun position
        gun.attack(null); // Attack logic (null player for example)
    }

    @Override
    public void render(Graphics g) {
        super.render(g);
        // Add additional rendering for the Spirit and its gun if needed
    }

	@Override
	public void damage(int amount) {
		// TODO Auto-generated method stub
		
	}
}