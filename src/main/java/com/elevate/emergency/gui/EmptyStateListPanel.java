package com.elevate.emergency.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Wraps a JList so that when its model is empty, a clear placeholder
 * message is shown instead of blank space -- so an empty triage queue,
 * for example, reads as "genuinely empty" rather than "looks broken".
 */
public class EmptyStateListPanel<T> extends JPanel {

    private static final String LIST_CARD = "list";
    private static final String EMPTY_CARD = "empty";

    private final CardLayout cardLayout = new CardLayout();
    private final JList<T> list;
    private final JLabel emptyLabel;

    public EmptyStateListPanel(JList<T> list, String emptyText, Dimension size) {
        this.list = list;
        setLayout(cardLayout);
        setOpaque(false);

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        scroll.setPreferredSize(size);
        scroll.getViewport().setBackground(Theme.PANEL_DARK);

        emptyLabel = new JLabel("<html><div style='text-align:center;'>" + emptyText + "</div></html>", SwingConstants.CENTER);
        emptyLabel.setFont(Theme.FONT_SMALL);
        emptyLabel.setForeground(Theme.TEXT_MUTED);
        emptyLabel.setPreferredSize(size);
        emptyLabel.setOpaque(true);
        emptyLabel.setBackground(Theme.PANEL_DARK);

        add(scroll, LIST_CARD);
        add(emptyLabel, EMPTY_CARD);
        cardLayout.show(this, EMPTY_CARD);
    }

    /** Call after updating the underlying list's model to switch between the list view and the placeholder. */
    public void refreshVisibility() {
        cardLayout.show(this, list.getModel().getSize() == 0 ? EMPTY_CARD : LIST_CARD);
    }
}
