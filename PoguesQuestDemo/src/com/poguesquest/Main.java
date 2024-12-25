package com.poguesquest;

import com.poguesquest.entities.Guardian;
import com.poguesquest.entities.Player;
import com.poguesquest.entities.Bullet;
import com.poguesquest.items.Gun;
import com.poguesquest.items.Weapon;
import com.poguesquest.utils.Camera;
import com.poguesquest.utils.KeyHandler;
import com.poguesquest.utils.MouseHandler;
import com.poguesquest.world.MapGenerator;
import com.poguesquest.world.EnemyGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.List;

public class Main extends JPanel implements Runnable {
    private Thread gameThread;
    private Player player;
    private BufferedImage walkingSpriteSheet, idleSpriteSheet;
    private KeyHandler keyHandler;
    private MouseHandler mouseHandler;
    private int[][] map;
    private Camera camera;
    private final int tileSize = 32; // Updated tile size
    private final int startX = 25;
    private final int startY = 25;
    private BufferedImage tileset;
    private List<Guardian> enemies;
    int minDistance;

    public Main() {
        SwingUtilities.invokeLater(() -> requestFocusInWindow());

        camera = new Camera(800, 600, 1.0);

        mouseHandler = new MouseHandler();
        keyHandler = new KeyHandler();

        walkingSpriteSheet = ImageLoader.loadCharacterSpriteSheet("Lyria", "Walk");
        idleSpriteSheet = ImageLoader.loadCharacterSpriteSheet("Lyria", "Idle");

        BufferedImage pistolSprite = ImageLoader.loadImage("/pistol.png");
        Gun pistol = new Gun("Pistol", pistolSprite, pistolSprite, 10, 300, camera);

        MapGenerator generator = new MapGenerator(50, 50, System.currentTimeMillis());
        map = generator.generateMap(500, startX, startY);

        player = new Player(startX * tileSize, startY * tileSize, walkingSpriteSheet, idleSpriteSheet, tileSize, camera);
        player.equipWeapon(pistol);

        // Initialize EnemyGenerator and generate enemies
        BufferedImage guardianWalkingSpriteSheet = ImageLoader.loadCharacterSpriteSheet("Guardian", "Walk");
        BufferedImage guardianIdleSpriteSheet = ImageLoader.loadCharacterSpriteSheet("Guardian", "Idle");
        EnemyGenerator enemyGenerator = new EnemyGenerator(map, 50, 5, System.currentTimeMillis(), player, guardianWalkingSpriteSheet, guardianIdleSpriteSheet);
        enemies = enemyGenerator.generateEnemies(15);

        System.out.println("Total enemies generated: " + enemies.size());

        setPreferredSize(new Dimension(800, 600));
        setFocusable(true);
        requestFocusInWindow();

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
        addKeyListener(keyHandler);

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                player.updateCursorPosition(mouseHandler.getCursorPosition());
            }
        });

        tileset = ImageLoader.loadImage("/WallSheet.png"); // Load your 16-piece tileset image

        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        while (true) {
            player.update(keyHandler.isUp(), keyHandler.isDown(), keyHandler.isLeft(), keyHandler.isRight(), mouseHandler.getCursorPosition(), map);

            for (Guardian enemy : enemies) {
                enemy.update(enemies);

                // Check collision with walls for the enemy
                if (enemy.isColliding(map, tileSize)) {
                    // Handle collision logic, such as stopping movement or adjusting position
                }
            }

            if (player.getEquippedWeapon() instanceof Gun gun) {
                gun.updateCursorPosition(mouseHandler.getCursorPosition());
                gun.updateGunTip(player.getGunPosition());

                if (mouseHandler.isShooting()) {
                    gun.attack(player);
                }
                gun.updateBullets(map, tileSize);

                // Check bullet collisions with enemies
                Iterator<Bullet> bulletIterator = gun.getBullets().iterator();
                while (bulletIterator.hasNext()) {
                    Bullet bullet = bulletIterator.next();
                    for (Guardian enemy : enemies) {
                        if (bullet.getHitbox().intersects(enemy.getHitbox())) {
                            enemy.damage(gun.getDamage());
                            bulletIterator.remove();
                            break;
                        }
                    }
                }
            }

            Point hitboxCenter = player.getHitboxCenter();
            camera.centerOnPlayer(hitboxCenter.x, hitboxCenter.y);

            camera.clampToBounds(map[0].length, map.length, tileSize);

            repaint();

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.scale(camera.getScaleFactor(), camera.getScaleFactor());
        g2d.translate(-camera.getX(), -camera.getY());

        renderMap(g2d);

        player.render(g2d);

        if (player.getEquippedWeapon() != null) {
            Weapon equippedWeapon = player.getEquippedWeapon();
            if (equippedWeapon instanceof Gun gun) {
                Point gunPosition = player.getGunPosition();
                gun.render(g2d, gunPosition.x, gunPosition.y, player.isFacingRight());
            }
        }

        if (player.getEquippedWeapon() instanceof Gun gun) {
            for (var bullet : gun.getBullets()) {
                bullet.render(g2d);
            }
        }

        // Render enemies
        for (Guardian enemy : enemies) {
            enemy.render(g2d);
        }
    }

    private void renderMap(Graphics g) {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                int tileValue = map[y][x];

                if (tileValue == 1) { // Floor
                    g.setColor(new Color(34, 32, 52)); // Set floor color to grey
                    g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
                } else { // Wall
                    // Calculate the tile index in the tileset based on the tileValue
                    int tilesetX = (tileValue % 4) * tileSize;
                    int tilesetY = (tileValue / 4) * tileSize;

                    g.drawImage(tileset, x * tileSize, y * tileSize, (x + 1) * tileSize, (y + 1) * tileSize,
                            tilesetX, tilesetY, tilesetX + tileSize, tilesetY + tileSize, null);
                }
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pogue's Quest");
        Main game = new Main();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}