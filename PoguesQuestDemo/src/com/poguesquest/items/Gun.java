package com.poguesquest.items;

import com.poguesquest.entities.Bullet;
import com.poguesquest.entities.Player;
import com.poguesquest.utils.RotationUtils;
import com.poguesquest.utils.Camera;
import com.poguesquest.utils.Collider;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Gun extends Weapon {
    private double angle; // Gun's rotation angle
    private Point2D gunTip; // Position of the gun's tip where bullets spawn
    private int ammo; // Ammo count
    private ArrayList<Bullet> bullets; // List of bullets fired by this gun
    private Point cursorPosition; // Current cursor position (updated constantly)
    private long lastShotTime; // Time when the last bullet was fired
    private int fireRate; // Delay between shots in milliseconds

    private BufferedImage gunSprite;
    private BufferedImage gunHorizontalFlipSprite;
    private Camera camera; // Reference to the camera
    private int orbitRadius; // Radius of the gun's orbit

    public Gun(String name, BufferedImage sprite, BufferedImage flipSprite, int damage, int ammo, Camera camera) {
        super(name, sprite, damage);
        this.angle = 0;
        this.gunTip = new Point2D.Double(0, 0); // Gun tip is initialized at (0, 0)
        this.ammo = ammo;
        this.bullets = new ArrayList<>();
        this.cursorPosition = new Point(0, 0); // Initialize cursor position to (0,0)
        this.lastShotTime = 0; // Initialize last shot time
        this.fireRate = 200; // Example fire rate (adjust as needed)
        this.gunSprite = sprite;
        this.gunHorizontalFlipSprite = flipSprite;
        this.camera = camera;
        this.orbitRadius = 10; // Example orbit radius (adjust as needed)
    }

    // Update the cursor position in real-time
    public void updateCursorPosition(Point cursorPosition) {
        if (cursorPosition != null) {
            // Transform cursor position from screen to world coordinates
            this.cursorPosition = new Point(camera.screenToWorldX(cursorPosition.x), camera.screenToWorldY(cursorPosition.y));
            updateGunTip(cursorPosition); // Update gun tip whenever cursor position is updated
        }
    }

    // Update the gun's tip position based on player and current cursor position
    public void updateGunTip(Point playerPosition) {
        if (cursorPosition == null) return;

        // Calculate the player center (for better positioning of the gun)
        int centerX = playerPosition.x; // Player's x position
        int centerY = playerPosition.y; // Player's y position

        // Update the gun angle based on the current mouse position (cursor)
        this.angle = Math.atan2(cursorPosition.y - centerY, cursorPosition.x - centerX);

        // Calculate new gun tip position based on orbit radius
        int gunTipX = (int) (centerX + Math.cos(angle) * orbitRadius);
        int gunTipY = (int) (centerY + Math.sin(angle) * orbitRadius);

        this.gunTip.setLocation(gunTipX, gunTipY);
    }

    // Render the gun
    @Override
    public void render(Graphics g, int playerX, int playerY, boolean facingRight) {
        // Update the gun tip position based on the current cursor position
        updateGunTip(new Point(playerX, playerY));

        // Determine if the gun should be mirrored horizontally based on player's facing direction
        boolean mirrorHorizontal = !facingRight;

        // Adjust the angle when mirrored
        double renderAngle = angle;
        if (mirrorHorizontal) {
            renderAngle = Math.PI - angle;
        }

        // Rotate the gun sprite according to the adjusted angle
        BufferedImage rotatedGun = RotationUtils.rotateImage(sprite, renderAngle);

        // Calculate render position to align with the player's position
        int renderX = (int) gunTip.getX() + 5 - rotatedGun.getWidth() / 2; // Adjust for horizontal positioning
        int renderY = (int) gunTip.getY() + 15 - rotatedGun.getHeight() / 2; // Adjust for vertical positioning

        // Create a Graphics2D object for advanced transformations
        Graphics2D g2d = (Graphics2D) g;

        // Save the original transform
        AffineTransform originalTransform = g2d.getTransform();

        // Apply transformations
        if (mirrorHorizontal) {
            // Flip horizontally by scaling -1 on the x-axis
            g2d.translate(renderX + rotatedGun.getWidth(), renderY);
            g2d.scale(-1, 1);
            g2d.drawImage(rotatedGun, 0 - 15, 0, null);
        } else {
            // Render normally
            g2d.drawImage(rotatedGun, renderX, renderY, null);
        }

        // Restore the original transform
        g2d.setTransform(originalTransform);
    }

    @Override
    public void attack(Player player) {
        long currentTime = System.currentTimeMillis();

        // Check if enough time has passed since the last shot
        if (currentTime - lastShotTime < fireRate) {
            return; // Not enough time has passed, so exit the method
        }

        lastShotTime = currentTime; // Update the last shot time

        // Ensure the gun tip position is updated before firing
        updateGunTip(player.getGunPosition());

        // Check if ammo is available
        if (ammo > 0) {
            ammo--; // Reduce ammo on fire

            // Calculate the firing angle at the time of shooting (using real-time cursor)
            double firingAngle = Math.atan2(cursorPosition.y - gunTip.getY(), cursorPosition.x - gunTip.getX());

            // Create a new bullet traveling towards the cursor at the time of shooting
            Bullet bullet = new Bullet(
                (int) gunTip.getX() + 10, (int) gunTip.getY() + 8, // Bullet spawn position (gun tip)
                firingAngle, // Firing angle based on real-time cursor position
                getDamage() // Bullet damage
            );

            bullets.add(bullet); // Add bullet to the list
        } else {
            System.out.println("Out of ammo!");
        }
    }

    // Update bullets fired by this gun
    public void updateBullets(int[][] map, int tileSize) {
        // Iterate and update each bullet
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            bullet.update(map, tileSize);

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
}