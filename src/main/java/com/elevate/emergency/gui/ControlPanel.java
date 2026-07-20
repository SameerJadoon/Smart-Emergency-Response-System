package com.elevate.emergency.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Top toolbar: start/pause, manual patient intake, incident reporting,
 * reset, a manual-control-mode toggle, an emergency-rate slider (with a
 * live numeric readout), and a light/dark theme toggle.
 */
public class ControlPanel extends JPanel {

    private boolean running = true;
    private boolean manualMode = false;
    private GradientButton toggleButton;
    private GradientButton manualModeButton;
    private JLabel rateReadout;

    public interface Callbacks {
        void onReportIncident();
        void onNewPatient();
        void onToggleRun(boolean running);
        void onReset();
        void onSpeedChange(double eventsPerSecond);
        void onToggleTheme();
        void onToggleManualMode(boolean manualMode);
    }

    public ControlPanel(Callbacks cb) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));
        setBackground(Theme.PANEL_DARK);
        setBorder(new EmptyBorder(8, 18, 8, 18));

        JLabel title = new JLabel("Smart Emergency Response & Hospital Management System");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_LIGHT);
        add(title);
        add(Box.createHorizontalStrut(16));

        GradientButton newPatient = new GradientButton("+ New Patient", Theme.ACCENT_PURPLE, Theme.ACCENT_PURPLE_DK);
        newPatient.addActionListener(e -> cb.onNewPatient());
        add(newPatient);

        GradientButton incidentButton = new GradientButton("Report Incident", Theme.ACCENT_RED, Theme.ACCENT_RED_DK);
        incidentButton.addActionListener(e -> cb.onReportIncident());
        add(incidentButton);

        toggleButton = new GradientButton("Pause", Theme.ACCENT_BLUE, Theme.ACCENT_BLUE_DK);
        toggleButton.addActionListener(e -> {
            running = !running;
            toggleButton.setText(running ? "Pause" : "Resume");
            cb.onToggleRun(running);
        });
        add(toggleButton);

        manualModeButton = new GradientButton("Enable Manual Control", Theme.ACCENT_GREY, Theme.ACCENT_GREY_DK);
        manualModeButton.addActionListener(e -> {
            manualMode = !manualMode;
            manualModeButton.setText(manualMode ? "Disable Manual Control" : "Enable Manual Control");
            cb.onToggleManualMode(manualMode);
        });
        add(manualModeButton);

        GradientButton resetButton = new GradientButton("Reset", Theme.ACCENT_GREY, Theme.ACCENT_GREY_DK);
        resetButton.addActionListener(e -> cb.onReset());
        add(resetButton);

        GradientButton themeButton = new GradientButton(Theme.darkMode ? "Light Mode" : "Dark Mode",
                Theme.ACCENT_GREY, Theme.ACCENT_GREY_DK);
        themeButton.addActionListener(e -> {
            cb.onToggleTheme();
            themeButton.setText(Theme.darkMode ? "Light Mode" : "Dark Mode");
        });
        add(themeButton);

        add(Box.createHorizontalStrut(6));
        JLabel speedLabel = new JLabel("Auto emergency rate:");
        speedLabel.setForeground(Theme.TEXT_MUTED);
        speedLabel.setFont(Theme.FONT_BODY);
        add(speedLabel);

        JSlider speedSlider = new JSlider(1, 10, 3);
        speedSlider.setOpaque(false);
        speedSlider.setPreferredSize(new Dimension(120, 24));
        add(speedSlider);

        rateReadout = new JLabel(formatRate(3 / 25.0));
        rateReadout.setForeground(Theme.TEXT_LIGHT);
        rateReadout.setFont(Theme.FONT_BODY);
        rateReadout.setPreferredSize(new Dimension(70, 20));
        add(rateReadout);

        speedSlider.addChangeListener(e -> {
            double rate = speedSlider.getValue() / 25.0;
            rateReadout.setText(formatRate(rate));
            cb.onSpeedChange(rate);
        });
    }

    private String formatRate(double eventsPerSecond) {
        return String.format("%.2f /s", eventsPerSecond);
    }
}
