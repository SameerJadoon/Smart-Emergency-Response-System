package com.elevate.emergency.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modal dialog for reporting a multi-casualty incident: a location, a
 * severity scale, and how many people are affected. Replaces the old
 * "spawn one random patient" button with something that models a real
 * mass-casualty event (a bus crash, a building fire, etc.) and lets the
 * dispatch system prioritize ambulances toward it accordingly.
 */
public class IncidentDialog extends JDialog {

    public interface IncidentCallback {
        void onSubmit(int locationNode, SimController.IncidentScale scale, int peopleAffected);
    }

    private final JComboBox<String> locationCombo;
    private final JComboBox<SimController.IncidentScale> scaleCombo =
            new JComboBox<>(SimController.IncidentScale.values());
    private final JSpinner affectedSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 15, 1));

    public IncidentDialog(Frame owner, IncidentCallback callback) {
        super(owner, "Report Incident", true);
        setSize(400, 380);
        setLocationRelativeTo(owner);
        setResizable(false);

        String[] locations = new String[SimController.NUM_NODES - 1];
        int idx = 0;
        for (int i = 0; i < SimController.NUM_NODES; i++) {
            if (i == SimController.HOSPITAL_NODE) continue;
            locations[idx++] = "Intersection N" + i;
        }
        locationCombo = new JComboBox<>(locations);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Theme.PANEL_DARK);
        content.setBorder(new EmptyBorder(20, 22, 20, 22));

        JLabel heading = new JLabel("Report a multi-casualty incident");
        heading.setFont(Theme.FONT_HEADING);
        heading.setForeground(Theme.TEXT_LIGHT);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(heading);
        content.add(Box.createVerticalStrut(4));
        JLabel sub = new JLabel("Creates multiple patients at once; higher scale = more critical victims + dispatch priority.");
        sub.setFont(Theme.FONT_SMALL);
        sub.setForeground(Theme.TEXT_MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(sub);
        content.add(Box.createVerticalStrut(16));

        content.add(fieldLabel("Location"));
        locationCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        locationCombo.setFont(Theme.FONT_BODY);
        content.add(locationCombo);
        content.add(Box.createVerticalStrut(16));

        content.add(fieldLabel("Incident Scale"));
        scaleCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        scaleCombo.setFont(Theme.FONT_BODY);
        scaleCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                            boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof SimController.IncidentScale) {
                    setText(((SimController.IncidentScale) value).label);
                }
                return this;
            }
        });
        content.add(scaleCombo);
        content.add(Box.createVerticalStrut(16));

        content.add(fieldLabel("People Affected"));
        affectedSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        affectedSpinner.setFont(Theme.FONT_BODY);
        content.add(affectedSpinner);
        content.add(Box.createVerticalStrut(24));

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonRow.setOpaque(false);
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        GradientButton cancel = new GradientButton("Cancel", Theme.ACCENT_GREY, Theme.ACCENT_GREY_DK);
        cancel.addActionListener(e -> dispose());

        GradientButton submit = new GradientButton("Report Incident", Theme.ACCENT_RED, Theme.ACCENT_RED_DK);
        submit.addActionListener(e -> {
            int locNode = locationCombo.getSelectedIndex() + 1; // skip hospital node 0
            SimController.IncidentScale scale = (SimController.IncidentScale) scaleCombo.getSelectedItem();
            int affected = (Integer) affectedSpinner.getValue();
            callback.onSubmit(locNode, scale, affected);
            dispose();
        });

        buttonRow.add(cancel);
        buttonRow.add(submit);
        content.add(buttonRow);

        setContentPane(content);
        getRootPane().setDefaultButton(submit);
    }

    private JComponent fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_SMALL);
        l.setForeground(Theme.TEXT_MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(new EmptyBorder(0, 0, 4, 0));
        return l;
    }
}
