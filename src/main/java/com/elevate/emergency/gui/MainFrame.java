package com.elevate.emergency.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Top-level window. Wires together the ControlPanel, CityMapPanel, and
 * SidePanel around a shared SimController, and drives everything forward
 * with a single Swing Timer (~30ms tick -> ~33 FPS animation).
 */
public class MainFrame extends JFrame {

    private SimController controller;
    private ControlPanel controlPanel;
    private CityMapPanel mapPanel;
    private SidePanel sidePanel;
    private Timer timer;
    private boolean running = true;
    private long lastTickNanos;

    public MainFrame() {
        super("Smart Emergency Response & Hospital Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1360, 820);
        setMinimumSize(new Dimension(1050, 680));
        setLocationRelativeTo(null);

        controller = new SimController();
        buildUI();
        startTimer();
    }

    private void buildUI() {
        getContentPane().removeAll();
        getContentPane().setBackground(Theme.BG_DARK);
        setLayout(new BorderLayout());

        controlPanel = new ControlPanel(new ControlPanel.Callbacks() {
            @Override public void onReportIncident() {
                new IncidentDialog(MainFrame.this, (locationNode, scale, peopleAffected) ->
                        controller.createIncident(locationNode, scale, peopleAffected)).setVisible(true);
            }

            @Override public void onNewPatient() {
                new PatientIntakeDialog(MainFrame.this, (name, locationNode, urgency) ->
                        controller.addManualPatient(name, locationNode, urgency)).setVisible(true);
            }

            @Override public void onToggleRun(boolean isRunning) { running = isRunning; }

            @Override public void onReset() { resetSimulation(); }

            @Override public void onSpeedChange(double eventsPerSecond) { controller.setEmergenciesPerSecond(eventsPerSecond); }

            @Override public void onToggleTheme() {
                Theme.toggle();
                rebuildUI();
            }

            @Override public void onToggleManualMode(boolean manualMode) {
                controller.setManualControlMode(manualMode);
            }
        });
        add(controlPanel, BorderLayout.NORTH);

        mapPanel = new CityMapPanel(controller);
        add(mapPanel, BorderLayout.CENTER);

        sidePanel = new SidePanel(controller);
        add(sidePanel, BorderLayout.EAST);

        revalidate();
        repaint();
    }

    /** Rebuilds all panels against the SAME controller (used for theme toggle -- keeps sim state). */
    private void rebuildUI() {
        buildUI();
    }

    /** Creates a brand-new controller and rebuilds the UI (used for the Reset button). */
    private void resetSimulation() {
        controller = new SimController();
        buildUI();
    }

    private void startTimer() {
        lastTickNanos = System.nanoTime();
        timer = new Timer(30, e -> {
            long now = System.nanoTime();
            long deltaMs = (now - lastTickNanos) / 1_000_000;
            lastTickNanos = now;
            if (running) {
                controller.tick(deltaMs, true);
            }
            mapPanel.repaint();
            sidePanel.refresh();
        });
        timer.start();
    }
}
