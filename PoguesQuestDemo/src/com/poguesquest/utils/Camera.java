package com.poguesquest.utils;

public class Camera {
    private double x; // Camera's top-left x-coordinate in world space
    private double y; // Camera's top-left y-coordinate in world space
    private final int viewportWidth; // Width of the visible area
    private final int viewportHeight; // Height of the visible area
    private double scaleFactor; // Zoom level (2.5 = 2.5x size)

    // Constructor
    public Camera(int viewportWidth, int viewportHeight, double scaleFactor) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.scaleFactor = scaleFactor;
    }

    // Update the camera position to center on the player
    public void centerOnPlayer(int playerX, int playerY) {
        x = playerX - (viewportWidth / 2.0) / scaleFactor;
        y = playerY - (viewportHeight / 2.0) / scaleFactor;
    }

    // Clamp camera position to prevent it from going out of bounds
    public void clampToBounds(int mapWidth, int mapHeight, int tileSize) {
        double maxX = mapWidth * tileSize - viewportWidth / scaleFactor;
        double maxY = mapHeight * tileSize - viewportHeight / scaleFactor;

        x = Math.max(0, Math.min(x, maxX));
        y = Math.max(0, Math.min(y, maxY));
    }

    // Getters for camera position
    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    // Set scale factor (zoom level)
    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public double getScaleFactor() {
        return scaleFactor;
    }

    // Translate world coordinates to screen coordinates
    public int worldToScreenX(double d) {
        return (int) ((d - x) * scaleFactor);
    }

    public int worldToScreenY(double d) {
        return (int) ((d - y) * scaleFactor);
    }

    // Translate screen coordinates to world coordinates
    public int screenToWorldX(int screenX) {
        return (int) (screenX / scaleFactor + x);
    }

    public int screenToWorldY(int screenY) {
        return (int) (screenY / scaleFactor + y);
    }
}