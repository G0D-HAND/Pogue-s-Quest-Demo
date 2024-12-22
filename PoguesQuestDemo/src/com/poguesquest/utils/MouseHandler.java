package com.poguesquest.utils;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;

public class MouseHandler extends MouseAdapter {
    private Point cursorPosition;
    private boolean mousePressed;

    public MouseHandler() {
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

    // Update cursor position
    private void updateCursorPosition(Point screenPosition) {
        cursorPosition = screenPosition;
    }

    public Point getCursorPosition() {
        return cursorPosition;
    }

    public boolean isShooting() {
        return mousePressed;
    }
}