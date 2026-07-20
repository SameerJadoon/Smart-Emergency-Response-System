package com.elevate.emergency.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * End-of-session report: every patient treated, which doctor treated
 * them, their urgency, and their response/treatment times -- plus an
 * export button that writes the same data to a CSV file on disk.
 */
public class ReportPanel extends JPanel {

    private final SimController controller;
    private final String[] columns = {
            "Patient ID", "Name", "Urgency", "Treated By", "Specialty",
            "Response (s)", "Treatment (s)", "Discharged At"
    };
    private final DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable table = new JTable(tableModel);
    private final JLabel summaryLabel = new JLabel();

    public ReportPanel(SimController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(0, 10));
        setBackground(Theme.BG_DARK);
        setBorder(new EmptyBorder(14, 12, 14, 12));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel title = new JLabel("Session Treatment Report");
        title.setFont(Theme.FONT_HEADING);
        title.setForeground(Theme.TEXT_LIGHT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(title);

        summaryLabel.setFont(Theme.FONT_BODY);
        summaryLabel.setForeground(Theme.TEXT_MUTED);
        summaryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(Box.createVerticalStrut(4));
        header.add(summaryLabel);

        add(header, BorderLayout.NORTH);

        table.setFont(Theme.FONT_SMALL);
        table.setRowHeight(24);
        table.getTableHeader().setFont(Theme.FONT_BODY);
        table.setFillsViewportHeight(true);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        GradientButton exportBtn = new GradientButton("Export Report (CSV)", Theme.ACCENT_GREEN, Theme.ACCENT_GREEN_DK);
        exportBtn.addActionListener(e -> exportToCsv());
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setOpaque(false);
        footer.add(exportBtn);
        add(footer, BorderLayout.SOUTH);
    }

    /** Called each animation frame (cheap: only rewrites the table if the row count changed). */
    public void refresh() {
        List<SimController.TreatmentRecord> records = controller.getTreatmentReport();

        summaryLabel.setText(String.format(
                "%d patients treated so far  |  average response time: %.1fs  |  simulated time: %s",
                records.size(), controller.getAverageResponseSeconds(), formatClock(controller.getSimTimeMs())));

        if (tableModel.getRowCount() == records.size()) return; // no new completions since last refresh

        tableModel.setRowCount(0);
        for (SimController.TreatmentRecord r : records) {
            tableModel.addRow(new Object[]{
                    r.patientId, r.patientName, r.urgencyLabel, r.doctorName, r.doctorSpecialty,
                    String.format("%.1f", r.responseSeconds), String.format("%.1f", r.treatmentSeconds),
                    formatClock(r.dischargeTimestamp)
            });
        }
    }

    private String formatClock(long ms) {
        long totalSec = ms / 1000;
        return String.format("%02d:%02d", totalSec / 60, totalSec % 60);
    }

    private void exportToCsv() {
        JFileChooser chooser = new JFileChooser();
        String defaultName = "emergency_response_report_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
        chooser.setSelectedFile(new java.io.File(defaultName));
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        try (PrintWriter writer = new PrintWriter(new FileWriter(chooser.getSelectedFile()))) {
            writer.println(String.join(",", columns));
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                StringBuilder line = new StringBuilder();
                for (int col = 0; col < columns.length; col++) {
                    if (col > 0) line.append(",");
                    line.append(csvEscape(String.valueOf(tableModel.getValueAt(row, col))));
                }
                writer.println(line);
            }
            JOptionPane.showMessageDialog(this, "Report exported to:\n" + chooser.getSelectedFile().getAbsolutePath(),
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not write file: " + ex.getMessage(),
                    "Export Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String csvEscape(String value) {
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
