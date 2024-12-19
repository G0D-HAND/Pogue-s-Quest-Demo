package main;

import javax.swing.JFrame;

public class GameFrame extends JFrame {
    public GameFrame() {
        add(new GamePanel()); // Adds the main game panel (will build this later)
        setSize(800, 600); // Set the window size
        setLocationRelativeTo(null); // Center the window
    }
}
