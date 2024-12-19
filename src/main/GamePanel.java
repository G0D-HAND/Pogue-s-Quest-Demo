package main;

import input.InputManager;
import utils.Constants;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener {
    private InputManager inputManager;
    private Timer timer;

    public GamePanel() {
        setPreferredSize(new Dimension(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
        setBackground(new Color(Constants.BACKGROUND_COLOR));

        inputManager = new InputManager();
        addKeyListener(inputManager);
        addMouseListener(inputManager);
        setFocusable(true);

        timer = new Timer(16, this); // Roughly 60 FPS
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }

    private void update() {
        // Placeholder for update logic (e.g., moving the player, handling collisions)
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        // Placeholder for drawing logic (e.g., drawing the player, enemies)
        g.setColor(new Color(Constants.ENEMY_COLOR));
        g.fillRect((getWidth() - 50) /2, (getHeight() - 50) /2, 50, 50); // Temporary enemy representation
    }
}
