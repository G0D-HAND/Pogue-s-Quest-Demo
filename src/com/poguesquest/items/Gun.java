package com.poguesquest.items;

import com.poguesquest.entities.Bullet;
import com.poguesquest.entities.Player;
import com.poguesquest.utils.RotationUtils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Gun extends Weapon {
    private double angle; // Gun's rotation angle
    private Point2D gunTip; // Position of the gun's tip where bullets spawn
    private int ammo; // Ammo count
    private ArrayList<Bullet> bullets; // List of bullets fired by this gun
    private Point cursorPosition; // Current cursor position (updated constantly)

    public Gun(String name, BufferedImage sprite, int damage, int ammo) {
        super(name, sprite, damage);
        this.angle = 0;
        this.gunTip = new Point2D.Double(0, 0); // Gun tip is initialized at (0, 0)
        this.ammo = ammo;
        this.bullets = new ArrayList<>();
        this.cursorPosition = new Point(0, 0); // Initialize cursor position
    }

    // Update the cursor position in real-time
    public void updateCursorPosition(Point cursorPosition) {
        this.cursorPosition = cursorPosition;
    }

    // Update the gun's tip position based on player and current cursor position
    public void updateGunTip(int playerX, int playerY, boolean facingRight) {
        // Calculate the player center (for better positioning of the gun)
        int centerX = playerX + 17; // Half width of hitbox
        int centerY = playerY + 21; // Player's stomach area (center of player)

        // Update the gun angle based on the current mouse position (cursor)
        this.angle = Math.atan2(cursorPosition.y - centerY, cursorPosition.x - centerX);

        // Adjust gun offset based on facing direction (left or right)
        int gunOffsetX = facingRight ? 10 : -10; // Adjust for facing direction (left or right)
        int gunOffsetY = -5; // Vertical offset for the gun (you can tweak this)

        // Offset for the gun's length (sprite width represents gun length)
        int gunLength = sprite.getWidth() / 2; // Use half of sprite's width as the gun's length
        int gunTipX = (int) (centerX + gunOffsetX + Math.cos(angle) * gunLength);
        int gunTipY = (int) (centerY + gunOffsetY + Math.sin(angle) * gunLength);

        this.gunTip.setLocation(gunTipX, gunTipY);
    }

    // Render the gun
    public void render(Graphics g, int playerX, int playerY, boolean facingRight) {
        // Update the gun tip position based on current cursor position
        updateGunTip(playerX, playerY, facingRight);

        // Rotate the gun sprite according to the angle
        BufferedImage rotatedGun = RotationUtils.rotateImage(sprite, angle);

        // Adjust render position to account for rotation and offsets
        int gunOffsetX = facingRight ? 10 : -10; // Adjust for facing direction
        int gunOffsetY = -5; // Adjust for gun's vertical offset
        int renderX = playerX + gunOffsetX - rotatedGun.getWidth() / 2;
        int renderY = playerY + gunOffsetY - rotatedGun.getHeight() / 2;

        // Draw the rotated gun
        g.drawImage(rotatedGun, renderX, renderY, null);
    }

    @Override
    public void attack(Player player) {
        // Update the cursor position based on player input
        updateCursorPosition(player.getCursorPosition());

        // Determine whether the player is facing right or left
        boolean facingRight = player.getCursorPosition().x >= player.getX() + 17; // Simplified check

        // Update the gun's tip position based on the current cursor position and player's direction
        updateGunTip(player.getX(), player.getY(), facingRight);

        // Check if ammo is available
        if (ammo > 0) {
            ammo--; // Reduce ammo on fire

            // Debugging info: show bullet spawn position and direction
            System.out.println("Bullet spawned at: " + gunTip.getX() + ", " + gunTip.getY());
            System.out.println("Firing towards: " + cursorPosition.x + ", " + cursorPosition.y);

            // Create a new bullet traveling in the direction of the cursor
            Bullet bullet = new Bullet(
                (int) gunTip.getX(), (int) gunTip.getY(), // Bullet spawn position (gun tip)
                cursorPosition.x, cursorPosition.y, // Target position (current mouse position)
                getDamage() // Bullet damage
            );

            bullets.add(bullet); // Add bullet to the list
            System.out.println("Gun fired! Ammo left: " + ammo);
        } else {
            System.out.println("Out of ammo!");
        }
    }

    // Update bullets fired by this gun
    public void updateBullets() {
        // Iterate and update each bullet
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            bullet.update();

            // Remove bullet if it goes out of bounds or is no longer alive
            if (!bullet.isAlive()) {
                bullets.remove(i);
                i--; // Adjust index to avoid skipping bullets
            }
        }
    }

    // Getter for bullets list
    public ArrayList<Bullet> getBullets() {
        return bullets;
    }

    // Getters for angle, gun tip position, and ammo count
    public double getAngle() {
        return angle;
    }

    public Point2D getGunTip() {
        return gunTip;
    }

    public int getAmmo() {
        return ammo;
    }

    @Override
    public void render(Graphics g, int playerX, int playerY) {
        // Placeholder for rendering without rotation
    }
}
