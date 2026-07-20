package com.elevate.emergency.gui;

import com.elevate.emergency.datastructures.Graph;
import com.elevate.emergency.model.Ambulance;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Renders the city road network, the hospital, and every ambulance's
 * live position as it moves along its current route.
 */
public class CityMapPanel extends JPanel {

    private final SimController controller;
    private static final int PAD = 60;

    public CityMapPanel(SimController controller) {
        this.controller = controller;
        setOpaque(true);
    }

    private Point nodePoint(int nodeId, int w, int h) {
        double nx = SimController.NODE_X[nodeId];
        double ny = SimController.NODE_Y[nodeId];
        int x = PAD + (int) (nx * (w - 2 * PAD));
        int y = PAD + (int) (ny * (h - 2 * PAD));
        return new Point(x, y);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        g.setPaint(new GradientPaint(0, 0, Theme.MAP_BG, w, h, Theme.MAP_BG_2));
        g.fillRect(0, 0, w, h);

        Graph graph = controller.getGraph();

        // --- Roads ---
        g.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(Theme.ROAD_COLOR);
        for (int i = 0; i < graph.getNumNodes(); i++) {
            for (Graph.Edge e : graph.getNeighbors(i)) {
                if (e.toNodeId > i) {
                    Point p1 = nodePoint(i, w, h);
                    Point p2 = nodePoint(e.toNodeId, w, h);
                    g.draw(new Line2D.Double(p1.x, p1.y, p2.x, p2.y));
                }
            }
        }

        g.setFont(Theme.FONT_SMALL);
        g.setColor(Theme.ROAD_LABEL);
        for (int i = 0; i < graph.getNumNodes(); i++) {
            for (Graph.Edge e : graph.getNeighbors(i)) {
                if (e.toNodeId > i) {
                    Point p1 = nodePoint(i, w, h);
                    Point p2 = nodePoint(e.toNodeId, w, h);
                    int mx = (p1.x + p2.x) / 2;
                    int my = (p1.y + p2.y) / 2;
                    g.drawString(String.valueOf((int) e.weight), mx + 4, my - 4);
                }
            }
        }

        // --- Nodes ---
        for (int i = 0; i < graph.getNumNodes(); i++) {
            Point p = nodePoint(i, w, h);
            boolean isHospital = i == SimController.HOSPITAL_NODE;
            int r = isHospital ? 24 : 14;

            g.setColor(new Color(0, 0, 0, 40));
            g.fill(new Ellipse2D.Double(p.x - r + 2, p.y - r + 4, r * 2, r * 2));

            if (isHospital) {
                g.setPaint(new GradientPaint(p.x - r, p.y - r, Theme.HOSPITAL_FILL_1, p.x + r, p.y + r, Theme.HOSPITAL_FILL_2));
            } else {
                g.setPaint(Theme.NODE_FILL);
            }
            g.fill(new Ellipse2D.Double(p.x - r, p.y - r, r * 2, r * 2));
            g.setColor(Theme.NODE_BORDER);
            g.setStroke(new BasicStroke(2f));
            g.draw(new Ellipse2D.Double(p.x - r, p.y - r, r * 2, r * 2));

            if (isHospital) {
                g.setColor(Color.WHITE);
                g.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawLine(p.x - 10, p.y, p.x + 10, p.y);
                g.drawLine(p.x, p.y - 10, p.x, p.y + 10);
                g.setFont(Theme.FONT_HEADING);
                g.setColor(Theme.TEXT_LIGHT);
                FontMetrics fm = g.getFontMetrics();
                String label = "Hospital";
                g.drawString(label, p.x - fm.stringWidth(label) / 2, p.y + 42);
            } else {
                g.setFont(Theme.FONT_SMALL);
                g.setColor(Theme.TEXT_MUTED);
                g.drawString("N" + i, p.x - 8, p.y - r - 6);
            }
        }

        // --- Ambulances ---
        java.util.List<Ambulance> fleet = controller.getFleet();
        for (int i = 0; i < fleet.size(); i++) {
            Ambulance a = fleet.get(i);
            double[] pos = controller.getAmbulancePosition(i);
            int x = PAD + (int) (pos[0] * (w - 2 * PAD));
            int y = PAD + (int) (pos[1] * (h - 2 * PAD));
            drawAmbulanceIcon(g, x, y, statusColor(a.getStatus()), a.getId());
        }
    }

    private Color statusColor(Ambulance.Status s) {
        switch (s) {
            case TO_PATIENT: return Theme.AMBULANCE_TO_PATIENT;
            case TO_HOSPITAL: return Theme.AMBULANCE_TO_HOSPITAL;
            default: return Theme.AMBULANCE_IDLE;
        }
    }

    /** Draws a small stylized ambulance glyph: rounded body + red cross + status-colored outline. */
    private void drawAmbulanceIcon(Graphics2D g, int x, int y, Color statusColor, String label) {
        int bw = 26, bh = 18;
        RoundRectangle2D body = new RoundRectangle2D.Double(x - bw / 2.0, y - bh / 2.0, bw, bh, 8, 8);

        g.setColor(new Color(0, 0, 0, 45));
        g.fill(new RoundRectangle2D.Double(x - bw / 2.0 + 1, y - bh / 2.0 + 3, bw, bh, 8, 8));

        g.setColor(Color.WHITE);
        g.fill(body);
        g.setStroke(new BasicStroke(2.4f));
        g.setColor(statusColor);
        g.draw(body);

        // red cross
        g.setColor(Theme.ACCENT_RED);
        g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(x - 5, y, x + 5, y);
        g.drawLine(x, y - 5, x, y + 5);

        g.setFont(new Font("Segoe UI", Font.BOLD, 9));
        g.setColor(Theme.TEXT_MUTED);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(label, x - fm.stringWidth(label) / 2, y + bh / 2 + 12);
    }
}
