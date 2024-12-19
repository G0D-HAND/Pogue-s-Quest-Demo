package com.poguesquest.entities;

import com.poguesquest.items.Weapon;
import com.poguesquest.utils.RotationUtils;
import com.poguesquest.utils.SpriteSheet;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player {
    private int x, y; // Player's position
    private int speed = 2; // Movement speed
    private int health = 100; // Player's health
    private int ammo = 30; // Ammo count

    private BufferedImage[] walkingFrames; // Walking animation frames
    private BufferedImage[] idleFrames;    // Idle animation frames
    private int currentFrame; // Current frame for animation
    private int frameDelay;   // Delay between frames for animation
    private boolean moving;   // Is the player moving?
    private boolean facingRight; // Direction the player is facing

    private Rectangle hitbox; // Hitbox for collision detection

    private Weapon equippedWeapon; // Currently equipped weapon (e.g., Gun)
    private Point cursorPosition; // Cursor position for aiming

    // Constructor
    public Player(int startX, int startY, BufferedImage walkingSpriteSheet, BufferedImage idleSpriteSheet) {
        this.x = startX;
        this.y = startY;

        // Initialize hitbox
        this.hitbox = new Rectangle(x, y, 35, 35);

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
        this.cursorPosition = new Point(0, 0); // Default cursor position
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
        this.cursorPosition = cursor;
        if (cursor.getX() < x + 35 / 2.0) { // Cursor is left of player center
            facingRight = false;
        } else { // Cursor is right of player center
            facingRight = true;
        }

        // Update hitbox position
        hitbox.setLocation(x, y);

        // Update animation frame
        frameDelay++;
        if (frameDelay > 5) {
            currentFrame++;
            if (moving) {
                currentFrame %= walkingFrames.length;
            } else {
                currentFrame %= idleFrames.length;
            }
            frameDelay = 0;
        }
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
        System.out.println("Equipped weapon: " + weapon.getName());
    }

    // Render method to display player animations, health, ammo, and hitbox
    public void render(Graphics g) {
        BufferedImage frame;

        if (moving) {
            frame = walkingFrames[currentFrame]; // Draw walking frame
        } else {
            frame = idleFrames[currentFrame]; // Draw idle frame
        }

        if (facingRight) {
            g.drawImage(frame, x, y, null); // Draw as-is if facing right
        } else {
            g.drawImage(flipHorizontally(frame), x, y, null); // Flip if facing left
        }

        // Debug: Draw hitbox
        g.setColor(Color.RED);
        g.drawRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        
        Weapon equippedWeapon = this.getEquippedWeapon();
        if (equippedWeapon != null) {
            // Calculate the center of the player
            int gunX = x + (frame.getWidth() / 2) - (equippedWeapon.getSprite().getWidth() / 2);
            int gunY = y + (frame.getHeight() / 2) - (equippedWeapon.getSprite().getHeight() / 2);

            // Rotate the gun to face the cursor
            double angle = Math.atan2(cursorPosition.y - y, cursorPosition.x - x); // Calculate angle to the cursor
            BufferedImage rotatedGun = RotationUtils.rotateImage(equippedWeapon.getSprite(), angle); // Rotate gun

            // Draw the rotated gun
            g.drawImage(rotatedGun, gunX, gunY, null);
        }
    }

    // Utility method to flip a BufferedImage horizontally
    private BufferedImage flipHorizontally(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage flipped = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = flipped.createGraphics();

        g2.drawImage(image, width, 0, -width, height, null);
        g2.dispose();

        return flipped;
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
}
