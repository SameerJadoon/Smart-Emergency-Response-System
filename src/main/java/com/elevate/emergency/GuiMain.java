package com.elevate.emergency;

import com.elevate.emergency.gui.MainFrame;

import javax.swing.*;

/**
 * Entry point for the graphical (Swing) version of the simulation.
 * Right-click this file in IntelliJ and choose "Run 'GuiMain.main()'"
 * -- no extra SDK or module setup required, Swing ships with the JDK.
 */
public class GuiMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // fall back to default look and feel
            }
            new MainFrame().setVisible(true);
        });
    }
}
