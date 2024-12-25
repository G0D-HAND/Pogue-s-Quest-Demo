package com.poguesquest.entities;

import com.poguesquest.items.Gun;
import com.poguesquest.items.Weapon;
import com.poguesquest.utils.SpriteSheet;
import com.poguesquest.utils.Camera;
import com.poguesquest.utils.Collider;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends Entity {
    private static final int SPEED = 4;
    private static final int MAX_HEALTH = 100;
    private int health = MAX_HEALTH;
    private int ammo = 30;
    private int tileSize;
    private BufferedImage[] walkingFrames;
    private BufferedImage[] idleFrames;
    private int currentFrame;
    private int frameDelay;
    private boolean moving;
    private boolean facingRight;
    private Collider collider;
    private Weapon equippedWeapon;
    private Point cursorPosition;
    private Camera camera;
    private int lineOfSight = 380; // Line of sight in pixels

    public Player(int startX, int startY, BufferedImage walkingSpriteSheet, BufferedImage idleSpriteSheet, int tileSize, Camera camera) {
        super(startX, startY, tileSize, tileSize);
        this.tileSize = tileSize;
        this.camera = camera;
        this.collider = new Collider(x + 5, y + 5, 25, 25);

        // Initialize sprite frames
        SpriteSheet walkingSheet = new SpriteSheet(walkingSpriteSheet);
        walkingFrames = new BufferedImage[8];
        for (int i = 0; i < 8; i++) {
            walkingFrames[i] = walkingSheet.getPlayerFrame(i);
        }

        SpriteSheet idleSheet = new SpriteSheet(idleSpriteSheet);
        idleFrames = new BufferedImage[8];
        for (int i = 0; i < 8; i++) {
            idleFrames[i] = idleSheet.getPlayerFrame(i);
        }

        this.facingRight = true;
        this.cursorPosition = new Point(0, 0);
    }

    public void update(boolean up, boolean down, boolean left, boolean right, Point cursor, int[][] map) {
        moving = false;

        int oldX = x;
        int oldY = y;

        if (up) {
            y -= SPEED;
            moving = true;
        }
        if (down) {
            y += SPEED;
            moving = true;
        }
        if (left) {
            x -= SPEED;
            moving = true;
            facingRight = false;
        }
        if (right) {
            x += SPEED;
            moving = true;
            facingRight = true;
        }

        collider.setPosition(x + 6, y + 10);

        if (collider.isColliding(map, tileSize)) {
            x = oldX;
            y = oldY;
        }

        if (cursor != null) {
            this.cursorPosition = new Point(camera.screenToWorldX(cursor.x), camera.screenToWorldY(cursor.y));
            if (cursorPosition.getX() < x + tileSize / 2.0) {
                facingRight = false;
            } else {
                facingRight = true;
            }
        }

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

        if (equippedWeapon instanceof Gun gun) {
            gun.updateCursorPosition(cursorPosition);
        }
    }

    public boolean isEnemyInSight(Enemy enemy) {
        double distance = getDistance(x, y, enemy.getX(), enemy.getY());
        return distance <= lineOfSight;
    }

    private double getDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public void damage(int amount) {
        health -= amount;
        if (health < 0) {
            health = 0;
        }
    }

    public Point getGunPosition() {
        int gunX = x + 10;
        int gunY = y + 10;
        return new Point(gunX, gunY);
    }

    public Point getHitboxCenter() {
        return new Point(collider.getHitbox().x + collider.getHitbox().width / 2, collider.getHitbox().y + collider.getHitbox().height / 2);
    }

    public void shoot() {
        if (equippedWeapon != null) {
            equippedWeapon.attack(this);
        } else {
            System.out.println("No weapon equipped!");
        }
    }

    public void equipWeapon(Weapon weapon) {
        this.equippedWeapon = weapon;
    }

    public void render(Graphics g) {
        BufferedImage frame;

        if (moving) {
            frame = walkingFrames[currentFrame];
        } else {
            frame = idleFrames[currentFrame];
        }

        if (facingRight) {
            g.drawImage(frame, x, y, null);
        } else {
            g.drawImage(flipHorizontally(frame), x + 10, y, null);
        }

        g.setColor(Color.RED);
    }

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
        return collider.getHitbox();
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

    @Override
    public void update() {

    }
}