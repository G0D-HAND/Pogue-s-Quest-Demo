package com.poguesquest.entities;

import com.poguesquest.items.Gun;
import com.poguesquest.items.Weapon;
import com.poguesquest.utils.SpriteSheet;
import com.poguesquest.utils.Camera;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player {
    private int x, y; // Player's position
    private int speed = 2; // Movement speed
    private int health = 100; // Player's health
    private int ammo = 30; // Ammo count
    private int tileSize; // Size of each tile

    private BufferedImage[] walkingFrames; // Walking animation frames
    private BufferedImage[] idleFrames;    // Idle animation frames
    private int currentFrame; // Current frame for animation
    private int frameDelay;   // Delay between frames for animation
    private boolean moving;   // Is the player moving?
    private boolean facingRight; // Direction the player is facing

    private Rectangle hitbox; // Hitbox for collision detection

    private Weapon equippedWeapon; // Currently equipped weapon (e.g., Gun)
    private Point cursorPosition; // Cursor position for aiming

    private Camera camera; // Reference to the camera

    // Constructor
    public Player(int startX, int startY, BufferedImage walkingSpriteSheet, BufferedImage idleSpriteSheet, int tileSize, Camera camera) {
        this.x = startX;
        this.y = startY;
        this.tileSize = tileSize;
        this.camera = camera;

        // Initialize hitbox
        this.hitbox = new Rectangle(x + 5, y + 5, 25, 25); // Adjusted position to keep it centered

        // Load walking animation frames
        SpriteSheet walkingSheet = new SpriteSheet(walkingSpriteSheet);
        walkingFrames = new BufferedImage[8];
        for (int i = 0; i < 8; i++) {
            walkingFrames[i] = walkingSheet.getFrame(i);
        }

        // Load idle animation frames
        SpriteSheet idleSheet = new SpriteSheet(idleSpriteSheet);
        idleFrames = new BufferedImage[8];
        for (int i = 0; i < 8; i++) {
            idleFrames[i] = idleSheet.getFrame(i);
        }

        this.facingRight = true;
        this.cursorPosition = new Point(0, 0); // Default cursor position (initialize cursorPosition)
    }

    // Update method for movement, aiming, and animations
    public void update(boolean up, boolean down, boolean left, boolean right, Point cursor) {
        moving = false;

        if (up) {
            y -= speed;
            moving = true;
        }
        if (down) {
            y += speed;
            moving = true;
        }
        if (left) {
            x -= speed;
            moving = true;
            facingRight = false; // Update facing direction
        }
        if (right) {
            x += speed;
            moving = true;
            facingRight = true; // Update facing direction
        }

        // Update facing direction based on cursor
        if (cursor != null) { // Ensure cursor is not null
            // Transform cursor position from screen to world coordinates
            this.cursorPosition = new Point(camera.screenToWorldX(cursor.x), camera.screenToWorldY(cursor.y)); 
            if (cursorPosition.getX() < x + tileSize / 2.0) { // Cursor is left of player center
                facingRight = false;
            } else { // Cursor is right of player center
                facingRight = true;
            }
        }

        // Update hitbox position (static relative to player position)
        hitbox.setLocation(x + 6, y + 10);

        // Update animation frame
        frameDelay++;
        if (frameDelay > 2) {
            currentFrame++;
            if (moving) {
                currentFrame %= walkingFrames.length;
            } else {
                currentFrame %= idleFrames.length;
            }
            frameDelay = 0;
        }

        // Update the gun position based on the cursor position
        if (equippedWeapon instanceof Gun gun) {
            gun.updateCursorPosition(cursorPosition);
        }
    }

    // Method to get the gun's position based on the player's position and direction
    public Point getGunPosition() {
        int gunX = x + 10; // Position the gun inside the player's hitbox
        int gunY = y + 10; // Adjust vertically
        return new Point(gunX, gunY);
    }

    // Method to get the center of the player's hitbox
    public Point getHitboxCenter() {
        return new Point(hitbox.x + hitbox.width / 2, hitbox.y + hitbox.height / 2);
    }

    // Shoot method (triggers attack if weapon is equipped)
    public void shoot() {
        if (equippedWeapon != null) {
            equippedWeapon.attack(this);
        } else {
            System.out.println("No weapon equipped!");
        }
    }

    // Equip a weapon
    public void equipWeapon(Weapon weapon) {
        this.equippedWeapon = weapon;
    }

    // Render method to display player animations, health, ammo, and hitbox
    public void render(Graphics g) {
        BufferedImage frame;

        if (moving) {
            frame = walkingFrames[currentFrame]; // Draw walking frame
        } else {
            frame = idleFrames[currentFrame]; // Draw idle frame
        }

        // Flip the player image if not facing right
        if (facingRight) {
            g.drawImage(frame, x, y, null); // Draw as-is if facing right
        } else {
            g.drawImage(flipHorizontally(frame), x + 10, y, null); // Flip if facing left
        }

        // Debug: Draw hitbox
        g.setColor(Color.RED);
        //g.drawRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

    // Utility method to flip a BufferedImage horizontally
    private BufferedImage flipHorizontally(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage flipped = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = flipped.createGraphics();

        g2.drawImage(image, width + 3, 0, -width, height, null);
        g2.dispose();

        return flipped;
    }

    public void updateCursorPosition(Point cursor) {
        if (cursor != null) {
            this.cursorPosition = cursor;
        }
    }
    
    // Getters for position, health, ammo, and hitbox
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getHealth() {
        return health;
    }

    public int getAmmo() {
        return ammo;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public Point getCursorPosition() {
        return cursorPosition;
    }
    
    public boolean isFacingRight() {
        return facingRight;
    }
}