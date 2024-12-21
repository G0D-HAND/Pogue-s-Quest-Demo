package com.poguesquest;

import com.poguesquest.entities.Player;
import com.poguesquest.items.Gun;
import com.poguesquest.items.Weapon;
import com.poguesquest.utils.Camera;
import com.poguesquest.utils.MapGenerator;
import com.poguesquest.utils.MouseHandler;
import com.poguesquest.utils.KeyHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class Main extends JPanel implements Runnable {
    private Thread gameThread;
    private Player player;
    private BufferedImage walkingSpriteSheet, idleSpriteSheet;
    private KeyHandler keyHandler;
    private MouseHandler mouseHandler;
    private int[][] map; // The generated map
    private Camera camera; // The camera for following the player
    private final int tileSize = 35; // Assuming each tile is 35x35 pixels
    private final int startX = 25; // Player starting X position in tiles
    private final int startY = 25; // Player starting Y position in tiles

    public Main() {
        SwingUtilities.invokeLater(() -> requestFocusInWindow());

        // Initialize the camera with a scale factor of 2.0
        camera = new Camera(800, 600, 2.0);

        mouseHandler = new MouseHandler(camera); // Pass the camera to the MouseHandler
        keyHandler = new KeyHandler();

        // Load sprite sheets
        walkingSpriteSheet = ImageLoader.loadCharacterSpriteSheet("Lyria", "Walk");
        idleSpriteSheet = ImageLoader.loadCharacterSpriteSheet("Lyria", "Idle");

        // Load the pistol sprite and create a gun
        BufferedImage pistolSprite = ImageLoader.loadImage("/pistol.png");
        Gun pistol = new Gun("Pistol", pistolSprite, pistolSprite, 10, 300); // Name, sprite, damage, ammo

        // Generate the map
        MapGenerator generator = new MapGenerator(50, 50, System.currentTimeMillis());
        map = generator.generateMap(500, startX, startY); // Player starts at the center (startX, startY)

        // Initialize the player at the center of the 3x3 area
        player = new Player(startX * tileSize, startY * tileSize, walkingSpriteSheet, idleSpriteSheet, tileSize);
        player.equipWeapon(pistol);

        setPreferredSize(new Dimension(800, 600));
        setFocusable(true);
        requestFocusInWindow();

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
        addKeyListener(keyHandler);

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                // Update cursor position based on mouse movement
                player.updateCursorPosition(mouseHandler.getCursorPosition());
            }
        });

        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        while (true) {
            player.update(
                keyHandler.isUp(), keyHandler.isDown(), keyHandler.isLeft(), keyHandler.isRight(),
                mouseHandler.getCursorPosition()
            );

            if (player.getEquippedWeapon() instanceof Gun gun) {
                // Update gun with the current cursor position
                gun.updateCursorPosition(mouseHandler.getCursorPosition());
                gun.updateGunTip(player.getGunPosition());

                if (mouseHandler.isShooting()) {
                    gun.attack(player); // Trigger attack method
                }
                gun.updateBullets(); // Update bullets for the equipped weapon
            }

            // Update the camera position to center on the player
            Point hitboxCenter = player.getHitboxCenter();
            camera.centerOnPlayer(hitboxCenter.x, hitboxCenter.y);

            // Clamp the camera to the map bounds
            camera.clampToBounds(map[0].length, map.length, tileSize); // Assuming each tile is 35x35 pixels

            repaint(); // Repaint the game screen

            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Scale the graphics context for the camera's zoom level
        Graphics2D g2d = (Graphics2D) g;
        g2d.scale(camera.getScaleFactor(), camera.getScaleFactor());

        // Translate the graphics context to the camera's position
        g2d.translate(-camera.getX(), -camera.getY());

        // Render the map
        renderMap(g2d);

        // Render player
        player.render(g2d);

        if (player.getEquippedWeapon() != null) {
            Weapon equippedWeapon = player.getEquippedWeapon();
            if (equippedWeapon instanceof Gun gun) {
                // Render the gun inside the player's hitbox
                Point gunPosition = player.getGunPosition();
                gun.render(g2d, gunPosition.x, gunPosition.y, player.isFacingRight());
            }
        }

        // Render bullets if a gun is equipped
        if (player.getEquippedWeapon() instanceof Gun gun) {
            for (var bullet : gun.getBullets()) {
                bullet.render(g2d);
            }
        }
    }

    private void renderMap(Graphics g) {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == 1) {
                    g.setColor(Color.GRAY); // Floor color
                } else {
                    g.setColor(Color.DARK_GRAY); // Wall color
                }
                g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize); // Assuming each tile is 35x35 pixels
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pogue's Quest");
        Main game = new Main();
        frame.add(game); // Add the game to the JFrame
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}