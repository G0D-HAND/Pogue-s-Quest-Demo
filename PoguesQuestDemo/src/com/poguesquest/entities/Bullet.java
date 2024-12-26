package com.poguesquest.entities;

import com.poguesquest.utils.Collider;
import com.poguesquest.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Bullet {
    private int x, y;
    private int speed = 10;
    private int directionX, directionY;
    private static final int DAMAGE = 2;
    private boolean isAlive = true;
    private Collider collider;
    private BufferedImage bulletImage;
    private BufferedImage[] hitEffectFrames;
    private boolean showHitEffect = false;
    private int hitEffectFrameIndex = 0;
    private int hitEffectFrameDelay = 5; // Number of updates per frame
    private int hitEffectFrameTimer = 0;

    public Bullet(int startX, int startY, double firingAngle) {
        this.x = startX;
        this.y = startY;

        this.directionX = (int) (Math.cos(firingAngle) * speed);
        this.directionY = (int) (Math.sin(firingAngle) * speed);
        this.collider = new Collider(x, y, 5, 5);

        // Load the bullet image
        this.bulletImage = ImageLoader.loadBulletImage();
        // Load the hit effect frames
        this.hitEffectFrames = ImageLoader.loadHitEffectFrames();
    }

    public void update(int[][] map, int tileSize, List<Entity> entities) {
        if (isAlive) {
            x += directionX;
            y += directionY;

            collider.setPosition(x, y);

            if (collider.isColliding(map, tileSize)) {
                isAlive = false;
                showHitEffect = true;
            }

            if (isAlive) {
                for (Entity entity : entities) {
                    if (collider.isColliding(entity.getHitbox())) {
                        entity.damage(DAMAGE);
                        isAlive = false;
                        showHitEffect = true;
                        break;
                    }
                }
            }
        } else if (showHitEffect) {
            hitEffectFrameTimer++;
            if (hitEffectFrameTimer >= hitEffectFrameDelay) {
                hitEffectFrameTimer = 0;
                hitEffectFrameIndex++;
                if (hitEffectFrameIndex >= hitEffectFrames.length) {
                    showHitEffect = false;
                }
            }
        }
    }

    public void render(Graphics g) {
        if (isAlive) {
            g.drawImage(bulletImage, x, y, null); // Draw the bullet image
        } else if (showHitEffect) {
            g.drawImage(hitEffectFrames[hitEffectFrameIndex], x, y, null); // Draw the hit effect frame
        }
    }

    public boolean isAlive() {
        return isAlive;
    }

    public Rectangle getHitbox() {
        return collider.getHitbox();
    }

    public int getDamage() {
        return DAMAGE;
    }
}