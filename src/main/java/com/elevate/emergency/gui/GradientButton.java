package com.elevate.emergency.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** A rounded button with a gradient fill and a subtle hover brighten effect. */
public class GradientButton extends JButton {

    private final Color top;
    private final Color bottom;
    private boolean hovered = false;

    public GradientButton(String text, Color top, Color bottom) {
        super(text);
        this.top = top;
        this.bottom = bottom;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(Theme.FONT_BODY);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color t = hovered ? brighten(top) : top;
        Color b = hovered ? brighten(bottom) : bottom;
        GradientPaint gp = new GradientPaint(0, 0, t, 0, getHeight(), b);
        g.setPaint(gp);
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        g.dispose();
        super.paintComponent(g0);
    }

    private Color brighten(Color c) {
        return new Color(
                Math.min(255, c.getRed() + 18),
                Math.min(255, c.getGreen() + 18),
                Math.min(255, c.getBlue() + 18));
    }
}
