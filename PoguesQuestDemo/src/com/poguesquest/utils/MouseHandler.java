package com.poguesquest.utils;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;

public class MouseHandler extends MouseAdapter {
    private Point cursorPosition;
    private boolean mousePressed;
    private Camera camera; // Add a reference to the Camera

    // Constructor to initialize the camera
    public MouseHandler(Camera camera) {
        this.camera = camera;
        this.cursorPosition = new Point(0, 0);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mousePressed = true;
        updateCursorPosition(e.getPoint());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mousePressed = false;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        updateCursorPosition(e.getPoint());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        updateCursorPosition(e.getPoint());
    }

    // Update cursor position based on camera position
    private void updateCursorPosition(Point screenPosition) {
        int worldX = camera.screenToWorldX(screenPosition.x);
        int worldY = camera.screenToWorldY(screenPosition.y);
        cursorPosition = new Point(worldX, worldY);
    }

    public Point getCursorPosition() {
        return cursorPosition;
    }

    public boolean isShooting() {
        return mousePressed;
    }
}