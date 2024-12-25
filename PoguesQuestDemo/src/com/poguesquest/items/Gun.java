package com.poguesquest.items;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.poguesquest.entities.Bullet;
import com.poguesquest.entities.Entity;
import com.poguesquest.entities.Player;
import com.poguesquest.utils.Camera;
import com.poguesquest.utils.RotationUtils;

public class Gun extends Weapon {
    private double angle;
    private Point2D gunTip;
    private int ammo;
    private ArrayList<Bullet> bullets;
    private Point cursorPosition;
    private long lastShotTime;
    private int fireRate;
    private BufferedImage gunSprite;
    private BufferedImage gunHorizontalFlipSprite;
    private Camera camera;
    private int orbitRadius;

    public Gun(String name, BufferedImage sprite, BufferedImage flipSprite, int damage, int ammo, Camera camera) {
        super(name, sprite, damage);
        this.angle = 0;
        this.gunTip = new Point2D.Double(0, 0);
        this.ammo = ammo;
        this.bullets = new ArrayList<>();
        this.cursorPosition = new Point(0, 0);
        this.lastShotTime = 0;
        this.fireRate = 500; // Adjust fire rate according to the gun type
        this.gunSprite = sprite;
        this.gunHorizontalFlipSprite = flipSprite;
        this.camera = camera;
        this.orbitRadius = 15; // Adjust orbit radius if needed
    }

    public void updateCursorPosition(Point cursorPosition) {
        if (cursorPosition != null) {
            this.cursorPosition = new Point(camera.screenToWorldX(cursorPosition.x), camera.screenToWorldY(cursorPosition.y));
            updateGunTip(cursorPosition);
        }
    }

    public void updateGunTip(Point playerPosition) {
        if (cursorPosition == null) return;

        int centerX = playerPosition.x;
        int centerY = playerPosition.y;

        this.angle = Math.atan2(cursorPosition.y - centerY, cursorPosition.x - centerX);

        int gunTipX = (int) (centerX + Math.cos(angle) * orbitRadius);
        int gunTipY = (int) (centerY + Math.sin(angle) * orbitRadius);

        this.gunTip.setLocation(gunTipX, gunTipY);
    }

    @Override
    public void render(Graphics g, int playerX, int playerY, boolean facingRight) {
        updateGunTip(new Point(playerX, playerY));

        boolean mirrorHorizontal = !facingRight;

        double renderAngle = angle;
        if (mirrorHorizontal) {
            renderAngle = Math.PI - angle;
        }

        BufferedImage rotatedGun = RotationUtils.rotateImage(gunSprite, renderAngle);

        int renderX = (int) gunTip.getX() + 5 - rotatedGun.getWidth() / 2;
        int renderY = (int) gunTip.getY() + 15 - rotatedGun.getHeight() / 2;

        Graphics2D g2d = (Graphics2D) g;
        AffineTransform originalTransform = g2d.getTransform();

        if (mirrorHorizontal) {
            g2d.translate(renderX + rotatedGun.getWidth(), renderY);
            g2d.scale(-1, 1);
            g2d.drawImage(rotatedGun, 0 - 15, 0, null);
        } else {
            g2d.drawImage(rotatedGun, renderX, renderY, null);
        }

        g2d.setTransform(originalTransform);
    }

    @Override
    public void attack(Player player) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastShotTime < fireRate) {
            return;
        }

        lastShotTime = currentTime;
        updateGunTip(player.getGunPosition());

        if (ammo > 0) {
            ammo--;

            double firingAngle = Math.atan2(cursorPosition.y - gunTip.getY() - 5, cursorPosition.x - gunTip.getX());
            
            // Add some random spread to the bullets
            double spread = Math.toRadians(10); // 5 degrees of spread
            firingAngle += (Math.random() - 1) * spread;

            Bullet bullet = new Bullet(
                (int) gunTip.getX() + 10, (int) gunTip.getY() + 8,
                firingAngle
            );

            bullets.add(bullet);
        } else {
            System.out.println("Out of ammo!");
        }
    }

    public void updateBullets(int[][] map, int tileSize) {
        List<Entity> emptyEntitiesList = new ArrayList<>();

        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            bullet.update(map, tileSize, emptyEntitiesList);

            if (!bullet.isAlive()) {
                bullets.remove(i);
                i--;
            }
        }
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }

    public void attack1(Player player) {
        // TODO Auto-generated method stub
        
    }
}