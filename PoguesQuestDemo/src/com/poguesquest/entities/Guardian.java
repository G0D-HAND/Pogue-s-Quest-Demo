package com.poguesquest.entities;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;
import com.poguesquest.utils.SpriteSheet;

public class Guardian extends Enemy {
    private static final int MOVEMENT_SPEED = 1;
    private static final int ATTACK_DAMAGE = 2;
    private static final int MAX_HEALTH = 20;
    private static final int LINE_OF_SIGHT = 100;
    private static final int ATTACK_COOLDOWN = 1000; // Cooldown period in milliseconds
    public static final int SEPARATION_DISTANCE = 50;
    private int health = MAX_HEALTH;
    private Player player;
    private BufferedImage[] walkingFrames;
    private BufferedImage[] idleFrames;
    private int currentFrame;
    private int frameDelay;
    private boolean moving;
    private boolean facingRight;
    private Rectangle hitbox;
    private int[][] map;
    private int currentLineOfSight;
    private Random random;
    private int wanderTime;
    private int wanderDirection;
    private GuardianState state;
    private long idleStartTime;
    private long lastAttackTime; // Time of the last attack
    private Rectangle boundaries; // Boundary for the guardian's movement

    private enum GuardianState {
        IDLE,
        WANDERING,
        PURSUING,
        RETREATING
    }

    public Guardian(int x, int y, int width, int height, BufferedImage walkingSpriteSheet, BufferedImage idleSpriteSheet, Player player, int[][] map, Rectangle boundaries) {
        super(x, y, width, height, LINE_OF_SIGHT, null);
        this.player = player;
        this.map = map;
        this.boundaries = boundaries;

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
        this.hitbox = new Rectangle(x, y, 25, 25);
        this.currentLineOfSight = LINE_OF_SIGHT;
        this.random = new Random();
        this.wanderTime = 0;
        this.wanderDirection = random.nextInt(4); // Random initial direction
        this.state = GuardianState.WANDERING; // Initial state
        this.lastAttackTime = 0; // Initialize the last attack time
    }

    @Override
    public void damage(int amount) {
        health -= amount;
        if (health <= 0) {
            health = 0;
            die();
        }
    }

    private void die() {
        // Handle death logic here, if any
    }

    public boolean isDead() {
        return health <= 0;
    }

    public void update(List<Guardian> guardians) {
        moving = false;

        // Maintain separation from other guardians
        separateFromOthers(guardians);

        if (player.isDead()) {
            state = GuardianState.WANDERING; // Reset to wandering if player is dead
        }

        switch (state) {
            case IDLE:
                handleIdleState();
                break;
            case WANDERING:
                wander();
                break;
            case PURSUING:
                pursuePlayer();
                break;
            case RETREATING:
                retreatFromPlayer();
                break;
        }

        // Transition to other states based on conditions
        if (!player.isDead()) {
            if (isHealthLow() && state != GuardianState.RETREATING) {
                state = GuardianState.RETREATING;
            } else if (isPlayerInSight() && state != GuardianState.RETREATING) {
                state = GuardianState.PURSUING;
            } else if (state == GuardianState.PURSUING && !isPlayerInSight()) {
                state = GuardianState.WANDERING;
            }
        }

        // Check for player collision and damage the player if close and cooldown has passed
        if (state == GuardianState.PURSUING && isPlayerClose()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastAttackTime >= ATTACK_COOLDOWN) {
                player.damage(ATTACK_DAMAGE);
                lastAttackTime = currentTime; // Update the last attack time
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

    private void handleIdleState() {
        if (System.currentTimeMillis() - idleStartTime > 3000) { // Check if 3 seconds have passed
            state = GuardianState.WANDERING;
        }
    }

    private void wander() {
        if (wanderTime <= 0) {
            // Choose a new direction to wander in
            wanderDirection = random.nextInt(4);
            wanderTime = random.nextInt(60) + 30; // Wander for a random duration between 30 and 90 frames
        } else {
            // Move in the current wandering direction
            switch (wanderDirection) {
                case 0: // Move up
                    if (canMoveTo(x, y - MOVEMENT_SPEED)) {
                        y -= MOVEMENT_SPEED;
                        moving = true;
                    }
                    break;
                case 1: // Move down
                    if (canMoveTo(x, y + MOVEMENT_SPEED)) {
                        y += MOVEMENT_SPEED;
                        moving = true;
                    }
                    break;
                case 2: // Move left
                    if (canMoveTo(x - MOVEMENT_SPEED, y)) {
                        x -= MOVEMENT_SPEED;
                        facingRight = false;
                        moving = true;
                    }
                    break;
                case 3: // Move right
                    if (canMoveTo(x + MOVEMENT_SPEED, y)) {
                        x += MOVEMENT_SPEED;
                        facingRight = true;
                        moving = true;
                    }
                    break;
            }
            wanderTime--;
        }
    }

    protected void updateHitbox() {
        hitbox.setLocation(x, y);
    }

    private boolean isPlayerInSight() {
        double distance = getDistance(x, y, player.getX(), player.getY());
        return distance <= currentLineOfSight;
    }

    private boolean isPlayerClose() {
        double distance = getDistance(x, y, player.getX(), player.getY());
        return distance <= 20; // Adjust this value as needed
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
        boolean withinBoundaries = boundaries == null || (newX >= boundaries.x && newX + hitbox.width <= boundaries.x + boundaries.width && newY >= boundaries.y && newY + hitbox.height <= boundaries.y + boundaries.height);
        return withinBoundaries && isTileWalkable(tileX, tileY) &&
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
        // Implement if needed
    }
}