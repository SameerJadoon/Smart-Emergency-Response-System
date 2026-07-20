package com.elevate.emergency.gui;

import javax.swing.*;
import java.awt.*;

/** A JPanel with a rounded-rectangle background, optionally gradient-filled, used as a "card" container. */
public class RoundedPanel extends JPanel {

    private Color colorA;
    private Color colorB; // null = flat fill using colorA
    private final int arc;

    public RoundedPanel(Color background, int arc) {
        this.colorA = background;
        this.colorB = null;
        this.arc = arc;
        setOpaque(false);
    }

    public RoundedPanel(Color top, Color bottom, int arc) {
        this.colorA = top;
        this.colorB = bottom;
        this.arc = arc;
        setOpaque(false);
    }

    public void setColors(Color top, Color bottom) {
        this.colorA = top;
        this.colorB = bottom;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (colorB != null) {
            g.setPaint(new GradientPaint(0, 0, colorA, 0, getHeight(), colorB));
        } else {
            g.setColor(colorA);
        }
        g.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        g.dispose();
        super.paintComponent(g0);
    }
}
