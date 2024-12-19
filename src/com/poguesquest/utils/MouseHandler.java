package com.poguesquest.utils;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseHandler extends MouseAdapter {
    private Point cursorPosition;
    private boolean isShooting; // Tracks if the left mouse button is pressed

    public MouseHandler() {
        cursorPosition = new Point(0, 0); // Initialize the cursor position
        isShooting = false; // Default to not shooting
    }

    public Point getCursorPosition() {
        return cursorPosition;
    }

    public boolean isShooting() {
        return isShooting;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        cursorPosition = e.getPoint();
    }

    @Override
    public void mousePressed(MouseEvent e) {

        if (e.getButton() == MouseEvent.BUTTON1) { // Left mouse button
            isShooting = true;
        }
    }


    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) { // Left mouse button
            isShooting = false;
        }
    }
}
