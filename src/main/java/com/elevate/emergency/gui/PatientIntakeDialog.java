package com.elevate.emergency.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Modal dialog for real patient intake: name, location on the road
 * network, and urgency level. On submit, hands the values back via the
 * supplied callback so SimController.spawnManualEmergency(...) can run.
 */
public class PatientIntakeDialog extends JDialog {

    public interface IntakeCallback {
        void onSubmit(String name, int locationNode, int urgency);
    }

    private final JTextField nameField = new JTextField();
    private final JComboBox<String> locationCombo;
    private final JComboBox<String> urgencyCombo = new JComboBox<>(new String[]{
            "1 - Critical", "2 - Severe", "3 - Moderate", "4 - Mild", "5 - Minor"
    });

    public PatientIntakeDialog(Frame owner, IntakeCallback callback) {
        super(owner, "New Patient Intake", true);
        setSize(380, 340);
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

        content.add(fieldLabel("Patient Name"));
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        nameField.setFont(Theme.FONT_BODY);
        content.add(nameField);
        content.add(Box.createVerticalStrut(16));

        content.add(fieldLabel("Location"));
        locationCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        locationCombo.setFont(Theme.FONT_BODY);
        content.add(locationCombo);
        content.add(Box.createVerticalStrut(16));

        content.add(fieldLabel("Urgency Level"));
        urgencyCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        urgencyCombo.setFont(Theme.FONT_BODY);
        content.add(urgencyCombo);
        content.add(Box.createVerticalStrut(24));

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonRow.setOpaque(false);
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        GradientButton cancel = new GradientButton("Cancel", Theme.ACCENT_GREY, Theme.ACCENT_GREY_DK);
        cancel.addActionListener(e -> dispose());

        GradientButton submit = new GradientButton("Call Ambulance", Theme.ACCENT_TEAL, Theme.ACCENT_TEAL_DK);
        submit.addActionListener(e -> {
            String name = nameField.getText();
            int locNode = locationCombo.getSelectedIndex() + 1; // skip hospital node 0
            int urgency = urgencyCombo.getSelectedIndex() + 1;
            callback.onSubmit(name, locNode, urgency);
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
