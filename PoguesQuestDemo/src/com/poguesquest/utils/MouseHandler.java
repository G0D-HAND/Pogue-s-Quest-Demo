package com.poguesquest.utils;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;

public class MouseHandler extends MouseAdapter {
    private Point cursorPosition;
    private boolean mousePressed;

    @Override
    public void mousePressed(MouseEvent e) {
        mousePressed = true;
        cursorPosition = e.getPoint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mousePressed = false;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        cursorPosition = e.getPoint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        cursorPosition = e.getPoint();
    }

    public Point getCursorPosition() {
        return cursorPosition;
    }

    public boolean isShooting() {
        return mousePressed;
    }
}