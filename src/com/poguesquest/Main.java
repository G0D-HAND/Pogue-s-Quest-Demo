package com.poguesquest;

import com.poguesquest.entities.Bullet;
import com.poguesquest.entities.Player;
import com.poguesquest.items.Gun;
import com.poguesquest.items.Weapon;
import com.poguesquest.utils.KeyHandler;
import com.poguesquest.utils.MouseHandler;

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

    public Main() {
        SwingUtilities.invokeLater(() -> requestFocusInWindow());

        mouseHandler = new MouseHandler();
        keyHandler = new KeyHandler();

        walkingSpriteSheet = ImageLoader.loadCharacterSpriteSheet("Lyria", "Walk");
        idleSpriteSheet = ImageLoader.loadCharacterSpriteSheet("Lyria", "Idle");

        // Load the pistol sprite and create a gun
        BufferedImage pistolSprite = ImageLoader.loadImage("/pistol.png");
        Gun pistol = new Gun("Pistol", pistolSprite, 10, 300); // Name, sprite, damage, ammo
        player = new Player(400, 300, walkingSpriteSheet, idleSpriteSheet);
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

            Weapon equippedWeapon = player.getEquippedWeapon();
            if (equippedWeapon instanceof Gun gun) {
                gun.updateCursorPosition(mouseHandler.getCursorPosition());
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
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Render player
        player.render(g);

        // Render the equipped weapon
        if (player.getEquippedWeapon() != null) {
            Weapon equippedWeapon = player.getEquippedWeapon();
            equippedWeapon.render(g, player.getX(), player.getY());
        }

        // Render bullets if a gun is equipped
        Weapon weapon = player.getEquippedWeapon();
        if (weapon instanceof Gun gun) {
            for (Bullet bullet : gun.getBullets()) {
                bullet.render(g);
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
