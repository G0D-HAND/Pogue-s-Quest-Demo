package com.poguesquest;

import com.poguesquest.entities.Player;
import com.poguesquest.items.Gun;
import com.poguesquest.items.Weapon;
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

    public Main() {
        SwingUtilities.invokeLater(() -> requestFocusInWindow());

        mouseHandler = new MouseHandler();
        keyHandler = new KeyHandler();

        // Load sprite sheets
        walkingSpriteSheet = ImageLoader.loadCharacterSpriteSheet("Lyria", "Walk");
        idleSpriteSheet = ImageLoader.loadCharacterSpriteSheet("Lyria", "Idle");

        // Load the pistol sprite and create a gun
        BufferedImage pistolSprite = ImageLoader.loadImage("/pistol.png");
        Gun pistol = new Gun("Pistol", pistolSprite, pistolSprite, 10, 300); // Name, sprite, damage, ammo
        player = new Player(400, 300, walkingSpriteSheet, idleSpriteSheet);
        player.equipWeapon(pistol);

        // Generate the map
        MapGenerator generator = new MapGenerator(50, 50, System.currentTimeMillis());
        map = generator.generateMap(500, 25, 25); // Example: 500 steps, starting at the center

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
                player.updateCursorPosition(e.getPoint());
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

        // Render the map
        renderMap(g);

        // Render player
        player.render(g);
        
        if (player.getEquippedWeapon() != null) {
            Weapon equippedWeapon = player.getEquippedWeapon();
            if (equippedWeapon instanceof Gun gun) {
                // Render the gun inside the player's hitbox
                Point gunPosition = player.getGunPosition();
                gun.render(g, gunPosition.x, gunPosition.y, player.isFacingRight());
            }
        }

        // Render bullets if a gun is equipped
        if (player.getEquippedWeapon() instanceof Gun gun) {
            for (var bullet : gun.getBullets()) {
                bullet.render(g);
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
                g.fillRect(x * 35, y * 35, 35, 35); // Assuming each tile is 16x16 pixels
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