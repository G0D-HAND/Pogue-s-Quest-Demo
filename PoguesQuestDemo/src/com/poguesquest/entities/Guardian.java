package com.poguesquest.entities;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

import com.poguesquest.utils.SpriteSheet;

public class Guardian extends Enemy {
    private static final int MOVEMENT_SPEED = 3;
    private static final int ATTACK_DAMAGE = 2;
    private static final int MAX_HEALTH = 5;
    public static final int SEPARATION_DISTANCE = 50;
    private int health = MAX_HEALTH;
    private int lineOfSight;
    private Player player;
    private BufferedImage[] walkingFrames;
    private BufferedImage[] idleFrames;
    private int currentFrame;
    private int frameDelay;
    private boolean moving;
    private boolean facingRight;
    private Rectangle hitbox;
    private int[][] map;

    public Guardian(int x, int y, int width, int height, BufferedImage walkingSpriteSheet, BufferedImage idleSpriteSheet, int lineOfSight, Player player, int[][] map) {
        super(x, y, width, height, lineOfSight, null);
        this.lineOfSight = lineOfSight;
        this.player = player;
        this.map = map;

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
        this.hitbox = new Rectangle(x, y, 25, 250);
    }

    @Override
    public void damage(int amount) {
        health -= amount;
        if (health < 0) {
            health = 0;
        }
    }

    public void update(List<Guardian> guardians) {
        moving = false;

        // Maintain separation from other guardians
        separateFromOthers(guardians);

        if (isHealthLow()) {
            retreatFromPlayer();
        } else if (isPlayerInSight()) {
            if (isPlayerClose()) {
                player.damage(ATTACK_DAMAGE);
            } else {
                pursuePlayer();
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

        updateHitbox();
    }

    protected void updateHitbox() {
        hitbox.setLocation(x, y);
    }

    private boolean isPlayerInSight() {
        double distance = getDistance(x, y, player.getX(), player.getY());
        return distance <= lineOfSight;
    }

    private boolean isPlayerClose() {
        double distance = getDistance(x, y, player.getX(), player.getY());
        return distance <= 20;
    }

    private boolean isHealthLow() {
        return health <= (MAX_HEALTH / 3);
    }

    private void pursuePlayer() {
        int playerX = player.getX();
        int playerY = player.getY();

        if (x < playerX && canMoveTo(x + MOVEMENT_SPEED, y)) {
            x += MOVEMENT_SPEED;
            facingRight = true;
        } else if (x > playerX && canMoveTo(x - MOVEMENT_SPEED, y)) {
            x -= MOVEMENT_SPEED;
            facingRight = false;
        }

        if (y < playerY && canMoveTo(x, y + MOVEMENT_SPEED)) {
            y += MOVEMENT_SPEED;
        } else if (y > playerY && canMoveTo(x, y - MOVEMENT_SPEED)) {
            y -= MOVEMENT_SPEED;
        }

        moving = true;
        updateHitbox();
    }

    private void retreatFromPlayer() {
        int playerX = player.getX();
        int playerY = player.getY();

        if (x < playerX && canMoveTo(x - MOVEMENT_SPEED, y)) {
            x -= MOVEMENT_SPEED;
            facingRight = false;
        } else if (x > playerX && canMoveTo(x + MOVEMENT_SPEED, y)) {
            x += MOVEMENT_SPEED;
            facingRight = true;
        }

        if (y < playerY && canMoveTo(x, y - MOVEMENT_SPEED)) {
            y -= MOVEMENT_SPEED;
        } else if (y > playerY && canMoveTo(x, y + MOVEMENT_SPEED)) {
            y += MOVEMENT_SPEED;
        }

        moving = true;
        updateHitbox();
    }

    private void retreatFromGuardian(Guardian other) {
        int otherX = other.getX();
        int otherY = other.getY();

        if (x < otherX && canMoveTo(x - MOVEMENT_SPEED, y)) {
            x -= MOVEMENT_SPEED;
            facingRight = false;
        } else if (x > otherX && canMoveTo(x + MOVEMENT_SPEED, y)) {
            x += MOVEMENT_SPEED;
            facingRight = true;
        }

        if (y < otherY && canMoveTo(x, y - MOVEMENT_SPEED)) {
            y -= MOVEMENT_SPEED;
        } else if (y > otherY && canMoveTo(x, y + MOVEMENT_SPEED)) {
            y += MOVEMENT_SPEED;
        }

        moving = true;
        updateHitbox();
    }

    private double getDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    @Override
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

    public Rectangle getHitbox() {
        return hitbox;
    }

    public boolean isColliding(int[][] map, int tileSize) {
        // Check the tiles around the current position to ensure no collision with walls
        int tileX = x / tileSize;
        int tileY = y / tileSize;

        // Check the corners of the hitbox
        return !isTileWalkable(tileX, tileY) ||
               !isTileWalkable((x + hitbox.width) / tileSize, tileY) ||
               !isTileWalkable(tileX, (y + hitbox.height) / tileSize) ||
               !isTileWalkable((x + hitbox.width) / tileSize, (y + hitbox.height) / tileSize);
    }

    private boolean canMoveTo(int newX, int newY) {
        // Check the tiles around the new position to ensure no collision with walls
        int tileSize = 32; // Assuming tile size is 32
        int tileX = newX / tileSize;
        int tileY = newY / tileSize;

        // Check the corners of the hitbox
        return isTileWalkable(tileX, tileY) &&
               isTileWalkable((newX + hitbox.width) / tileSize, tileY) &&
               isTileWalkable(tileX, (newY + hitbox.height) / tileSize) &&
               isTileWalkable((newX + hitbox.width) / tileSize, (newY + hitbox.height) / tileSize);
    }

    private boolean isTileWalkable(int tileX, int tileY) {
        // Assuming 0 is a wall in the map
        return tileX >= 0 && tileY >= 0 && tileX < map[0].length && tileY < map.length && map[tileY][tileX] == 1;
    }

    public void separateFromOthers(List<Guardian> guardians) {
        for (Guardian other : guardians) {
            if (other != this) {
                double distance = getDistance(x, y, other.x, other.y);
                if (distance < SEPARATION_DISTANCE) {
                    retreatFromGuardian(other);
                }
            }
        }
    }

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
}