package com.poguesquest.entities;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Clockwerk extends Enemy {

    public Clockwerk(int x, int y, int width, int height, int health, BufferedImage sprite) {
        super(x, y, width, height, health, sprite);
    }

    @Override
    public void update() {
        // Implement miniboss behavior here
        // Example: Complex attack patterns, higher health, etc.
    }

    @Override
    public void render(Graphics g) {
        super.render(g);
        // Add additional rendering for the Clockwerk if needed
    }

	@Override
	public void damage(int amount) {
		// TODO Auto-generated method stub
		
	}
}