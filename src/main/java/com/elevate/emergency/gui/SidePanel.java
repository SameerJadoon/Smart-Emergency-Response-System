package com.elevate.emergency.gui;

import com.elevate.emergency.model.Ambulance;
import com.elevate.emergency.model.Doctor;
import com.elevate.emergency.model.Patient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Right-hand dashboard with three tabs:
 *  - "Live": stats (including current mode + emergency rate), triage
 *     queue (critical patients pulse; shows a clear placeholder when
 *     empty rather than blank space), fleet/doctor status, event log.
 *  - "Manage": add/remove doctors, manually assign a specific ambulance
 *     to a specific call, or a specific doctor to a specific patient.
 *  - "Report": full end-of-session treatment table with CSV export.
 */
public class SidePanel extends JPanel {

    private final SimController controller;
    private final ReportPanel reportPanel;

    // Live tab widgets
    private final JLabel statMode = new JLabel();
    private final JLabel statClock = new JLabel();
    private final JLabel statRate = new JLabel();
    private final JLabel statAvgResponse = new JLabel();
    private final JLabel statCompleted = new JLabel();
    private final JLabel statPending = new JLabel();

    private final DefaultListModel<Patient> triageModel = new DefaultListModel<>();
    private final JList<Patient> triageList = new JList<>(triageModel);
    private final EmptyStateListPanel<Patient> triageWrap;

    private final DefaultListModel<String> fleetModel = new DefaultListModel<>();
    private final DefaultListModel<String> doctorStatusModel = new DefaultListModel<>();
    private final DefaultListModel<String> logModel = new DefaultListModel<>();

    // Manage tab widgets
    private final DefaultListModel<Doctor> rosterModel = new DefaultListModel<>();
    private final JList<Doctor> rosterList = new JList<>(rosterModel);
    private final EmptyStateListPanel<Doctor> rosterWrap;
    private final JTextField newDoctorName = new JTextField();
    private final JTextField newDoctorSpecialty = new JTextField();

    private final DefaultListModel<Patient> pendingCallsModel = new DefaultListModel<>();
    private final JList<Patient> pendingCallsList = new JList<>(pendingCallsModel);
    private final EmptyStateListPanel<Patient> pendingCallsWrap;

    private final DefaultListModel<Ambulance> idleAmbulanceModel = new DefaultListModel<>();
    private final JList<Ambulance> idleAmbulanceList = new JList<>(idleAmbulanceModel);
    private final EmptyStateListPanel<Ambulance> idleAmbulanceWrap;

    private final DefaultListModel<Patient> waitingForDoctorModel = new DefaultListModel<>();
    private final JList<Patient> waitingForDoctorList = new JList<>(waitingForDoctorModel);
    private final EmptyStateListPanel<Patient> waitingForDoctorWrap;

    private final DefaultListModel<Doctor> availableDoctorModel = new DefaultListModel<>();
    private final JList<Doctor> availableDoctorList = new JList<>(availableDoctorModel);
    private final EmptyStateListPanel<Doctor> availableDoctorWrap;

    public SidePanel(SimController controller) {
        this.controller = controller;
        this.reportPanel = new ReportPanel(controller);
        setLayout(new BorderLayout());
        setBackground(Theme.BG_DARK);
        setPreferredSize(new Dimension(370, 0));

        triageList.setBackground(Theme.PANEL_DARK);
        triageList.setCellRenderer(new PatientCellRenderer());
        triageList.setFixedCellHeight(28);
        triageWrap = new EmptyStateListPanel<>(triageList,
                "No patients currently waiting for a doctor.<br>They'll appear here once an ambulance drops them off.",
                new Dimension(320, 140));

        rosterList.setBackground(Theme.PANEL_DARK);
        rosterList.setCellRenderer(new DoctorCellRenderer());
        rosterList.setFixedCellHeight(24);
        rosterWrap = new EmptyStateListPanel<>(rosterList, "No doctors on the roster.", new Dimension(320, 90));

        pendingCallsList.setBackground(Theme.PANEL_DARK);
        pendingCallsList.setCellRenderer(new PatientCellRenderer());
        pendingCallsList.setFixedCellHeight(26);
        pendingCallsWrap = new EmptyStateListPanel<>(pendingCallsList,
                "No calls waiting for an ambulance right now.", new Dimension(320, 70));

        idleAmbulanceList.setCellRenderer(new AmbulanceCellRenderer());
        idleAmbulanceList.setBackground(Theme.PANEL_DARK);
        idleAmbulanceList.setFixedCellHeight(22);
        idleAmbulanceWrap = new EmptyStateListPanel<>(idleAmbulanceList,
                "No idle ambulances -- all are currently on a call.", new Dimension(320, 60));

        waitingForDoctorList.setBackground(Theme.PANEL_DARK);
        waitingForDoctorList.setCellRenderer(new PatientCellRenderer());
        waitingForDoctorList.setFixedCellHeight(26);
        waitingForDoctorWrap = new EmptyStateListPanel<>(waitingForDoctorList,
                "No patients waiting for a doctor right now.", new Dimension(320, 70));

        availableDoctorList.setBackground(Theme.PANEL_DARK);
        availableDoctorList.setCellRenderer(new DoctorCellRenderer());
        availableDoctorList.setFixedCellHeight(22);
        availableDoctorWrap = new EmptyStateListPanel<>(availableDoctorList,
                "No doctors currently available -- all are treating patients.", new Dimension(320, 60));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Theme.FONT_BODY);
        tabs.addTab("Live", buildLiveTab());
        tabs.addTab("Manage", buildManageTab());
        tabs.addTab("Report", reportPanel);
        add(tabs, BorderLayout.CENTER);
    }

    // ---------------------------------------------------------------
    // Live tab
    // ---------------------------------------------------------------

    private JComponent buildLiveTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Theme.BG_DARK);
        panel.setBorder(new EmptyBorder(14, 12, 14, 12));

        panel.add(buildStatsCard());
        panel.add(Box.createVerticalStrut(12));
        panel.add(buildTriageCard());
        panel.add(Box.createVerticalStrut(12));
        panel.add(buildFleetCard());
        panel.add(Box.createVerticalStrut(12));
        panel.add(buildLogCard());

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(Theme.BG_DARK);
        return scroll;
    }

    private JComponent buildStatsCard() {
        RoundedPanel p = card(Theme.ACCENT_BLUE_DK, Theme.PANEL_DARK);
        p.add(sectionTitle("Live Stats", Color.WHITE));
        p.add(Box.createVerticalStrut(8));
        for (JLabel lbl : new JLabel[]{statMode, statClock, statRate, statAvgResponse, statCompleted, statPending}) {
            lbl.setFont(Theme.FONT_BODY);
            lbl.setForeground(new Color(255, 255, 255, 220));
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            p.add(lbl);
            p.add(Box.createVerticalStrut(4));
        }
        return p;
    }

    private JComponent buildTriageCard() {
        RoundedPanel p = card(Theme.PANEL_DARK, Theme.PANEL_DARK);
        p.add(sectionTitle("Triage Queue (by urgency)", Theme.TEXT_LIGHT));
        p.add(Box.createVerticalStrut(6));
        p.add(triageWrap);
        return p;
    }

    private JComponent buildFleetCard() {
        RoundedPanel p = card(Theme.PANEL_DARK, Theme.PANEL_DARK);
        p.add(sectionTitle("Ambulances & Doctors", Theme.TEXT_LIGHT));
        p.add(Box.createVerticalStrut(6));

        JList<String> fleetList = new JList<>(fleetModel);
        styleSimpleList(fleetList);
        fleetList.setFixedCellHeight(20);
        JScrollPane fleetScroll = new JScrollPane(fleetList);
        fleetScroll.setBorder(null);
        fleetScroll.setPreferredSize(new Dimension(310, 90));
        fleetScroll.getViewport().setBackground(Theme.PANEL_DARK);
        p.add(fleetScroll);

        p.add(Box.createVerticalStrut(8));
        JList<String> docList = new JList<>(doctorStatusModel);
        styleSimpleList(docList);
        docList.setFixedCellHeight(20);
        JScrollPane docScroll = new JScrollPane(docList);
        docScroll.setBorder(null);
        docScroll.setPreferredSize(new Dimension(310, 70));
        docScroll.getViewport().setBackground(Theme.PANEL_DARK);
        p.add(docScroll);
        return p;
    }

    private JComponent buildLogCard() {
        RoundedPanel p = card(Theme.PANEL_DARK, Theme.PANEL_DARK);
        p.add(sectionTitle("Event Log", Theme.TEXT_LIGHT));
        p.add(Box.createVerticalStrut(6));

        JList<String> log = new JList<>(logModel);
        styleSimpleList(log);
        log.setFont(Theme.FONT_MONO);
        log.setFixedCellHeight(18);
        JScrollPane scroll = new JScrollPane(log);
        scroll.setBorder(null);
        scroll.setPreferredSize(new Dimension(310, 200));
        scroll.getViewport().setBackground(Theme.PANEL_DARK);
        p.add(scroll);
        return p;
    }

    // ---------------------------------------------------------------
    // Manage tab
    // ---------------------------------------------------------------

    private JComponent buildManageTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Theme.BG_DARK);
        panel.setBorder(new EmptyBorder(14, 12, 14, 12));

        panel.add(buildDoctorRosterCard());
        panel.add(Box.createVerticalStrut(12));
        panel.add(buildManualDispatchCard());
        panel.add(Box.createVerticalStrut(12));
        panel.add(buildManualAssignCard());

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(Theme.BG_DARK);
        return scroll;
    }

    private JComponent buildDoctorRosterCard() {
        RoundedPanel p = card(Theme.ACCENT_PURPLE_DK, Theme.PANEL_DARK);
        p.add(sectionTitle("Doctor Roster", Color.WHITE));
        p.add(Box.createVerticalStrut(6));
        p.add(rosterWrap);

        GradientButton removeBtn = new GradientButton("Remove Selected (if free)", Theme.ACCENT_GREY, Theme.ACCENT_GREY_DK);
        removeBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        removeBtn.addActionListener(e -> controller.removeDoctor(rosterList.getSelectedValue()));
        p.add(Box.createVerticalStrut(8));
        p.add(removeBtn);

        p.add(Box.createVerticalStrut(10));
        p.add(smallLabel("Add a doctor:", Color.WHITE));
        styleTextField(newDoctorName);
        styleTextField(newDoctorSpecialty);
        p.add(Box.createVerticalStrut(4));
        p.add(newDoctorName);
        p.add(Box.createVerticalStrut(4));
        p.add(newDoctorSpecialty);
        p.add(Box.createVerticalStrut(6));

        GradientButton addBtn = new GradientButton("Add Doctor", Theme.ACCENT_PURPLE, Theme.ACCENT_PURPLE_DK);
        addBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        addBtn.addActionListener(e -> {
            String name = newDoctorName.getText().trim();
            if (name.isEmpty()) return;
            String specialty = newDoctorSpecialty.getText().trim();
            controller.addDoctor(name, specialty.isEmpty() ? "General Medicine" : specialty);
            newDoctorName.setText("");
            newDoctorSpecialty.setText("");
        });
        p.add(addBtn);
        return p;
    }

    private JComponent buildManualDispatchCard() {
        RoundedPanel p = card(Theme.ACCENT_TEAL_DK, Theme.PANEL_DARK);
        p.add(sectionTitle("Manual Ambulance Dispatch", Color.WHITE));
        p.add(Box.createVerticalStrut(6));

        p.add(smallLabel("Pending calls:", Color.WHITE));
        p.add(pendingCallsWrap);

        p.add(Box.createVerticalStrut(6));
        p.add(smallLabel("Idle ambulances:", Color.WHITE));
        p.add(idleAmbulanceWrap);

        p.add(Box.createVerticalStrut(8));
        GradientButton dispatchBtn = new GradientButton("Dispatch Selected Ambulance", Theme.ACCENT_TEAL, Theme.ACCENT_TEAL_DK);
        dispatchBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        dispatchBtn.addActionListener(e -> {
            Patient patient = pendingCallsList.getSelectedValue();
            Ambulance ambulance = idleAmbulanceList.getSelectedValue();
            if (patient == null || ambulance == null) {
                JOptionPane.showMessageDialog(this,
                        "Click a pending call in the top list AND an idle ambulance in the bottom list first.",
                        "Nothing selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!controller.manualDispatch(patient, ambulance)) {
                JOptionPane.showMessageDialog(this,
                        "Could not dispatch -- that call or ambulance is no longer available.",
                        "Dispatch failed", JOptionPane.WARNING_MESSAGE);
            }
        });
        p.add(dispatchBtn);
        return p;
    }
    private JComponent buildManualAssignCard() {
        RoundedPanel p = card(Theme.ACCENT_BLUE_DK, Theme.PANEL_DARK);
        p.add(sectionTitle("Manual Doctor Assignment", Color.WHITE));
        p.add(Box.createVerticalStrut(6));

        p.add(smallLabel("Waiting patients:", Color.WHITE));
        p.add(waitingForDoctorWrap);

        p.add(Box.createVerticalStrut(6));
        p.add(smallLabel("Available doctors:", Color.WHITE));
        p.add(availableDoctorWrap);

        p.add(Box.createVerticalStrut(8));
        GradientButton assignBtn = new GradientButton("Assign Selected Doctor", Theme.ACCENT_BLUE, Theme.ACCENT_BLUE_DK);
        assignBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        assignBtn.addActionListener(e -> {
            Patient patient = waitingForDoctorList.getSelectedValue();
            Doctor doctor = availableDoctorList.getSelectedValue();
            if (patient == null || doctor == null) {
                JOptionPane.showMessageDialog(this,
                        "Click a waiting patient in the top list AND an available doctor in the bottom list first.",
                        "Nothing selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!controller.manualAssignDoctor(patient, doctor)) {
                JOptionPane.showMessageDialog(this,
                        "Could not assign -- that patient or doctor is no longer available.",
                        "Assignment failed", JOptionPane.WARNING_MESSAGE);
            }
        });
        p.add(assignBtn);
        return p;
    }

    // ---------------------------------------------------------------
    // Shared helpers
    // ---------------------------------------------------------------

    private RoundedPanel card(Color gradTop, Color gradBottom) {
        RoundedPanel p = new RoundedPanel(gradTop, gradBottom, 16);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(12, 14, 12, 14));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        return p;
    }

    private JComponent sectionTitle(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_HEADING);
        l.setForeground(color);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JComponent smallLabel(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_SMALL);
        l.setForeground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 200));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void styleSimpleList(JList<?> list) {
        list.setBackground(Theme.PANEL_DARK);
        list.setForeground(Theme.TEXT_MUTED);
        list.setFont(Theme.FONT_SMALL);
        list.setSelectionBackground(Theme.PANEL_DARK_ALT);
    }

    private void styleTextField(JTextField field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setFont(Theme.FONT_BODY);
    }

    private <T> void syncListPreservingSelection(JList<T> list, DefaultListModel<T> model, List<T> items) {
        // Skip the rebuild entirely if the contents are identical to what's
        // already shown -- this is what keeps your selection stable while
        // you're clicking an item and then pressing Dispatch/Assign. Without
        // this check, the model was being cleared and rebuilt ~30 times a
        // second even when nothing changed, which could wipe out a selection
        // made a split second earlier.
        if (model.getSize() == items.size()) {
            boolean identical = true;
            for (int i = 0; i < items.size(); i++) {
                if (model.getElementAt(i) != items.get(i)) {
                    identical = false;
                    break;
                }
            }
            if (identical) return;
        }

        T selected = list.getSelectedValue();
        model.clear();
        for (T item : items) model.addElement(item);
        if (selected != null && items.contains(selected)) {
            list.setSelectedValue(selected, false);
        }
    }

    /** Called every animation frame to push fresh data into every widget. */
    public void refresh() {
        statMode.setText(controller.isManualControlMode() ? "Mode: MANUAL CONTROL" : "Mode: Automatic");
        long totalSec = controller.getSimTimeMs() / 1000;
        statClock.setText(String.format("Simulated time: %02d:%02d", totalSec / 60, totalSec % 60));
        statRate.setText(String.format("Auto emergency rate: %.2f / sec%s",
                controller.getEmergenciesPerSecond(), controller.isManualControlMode() ? " (paused - manual mode)" : ""));
        statAvgResponse.setText(String.format("Avg response time: %.1fs", controller.getAverageResponseSeconds()));
        statCompleted.setText("Patients treated: " + controller.getCompletedCount());
        statPending.setText("Calls waiting for ambulance: " + controller.getPendingCalls().size());

        syncListPreservingSelection(triageList, triageModel, controller.getWaitingRoom());
        triageWrap.refreshVisibility();
        triageList.repaint(); // keeps the critical-urgency glow animating even without data changes

        fleetModel.clear();
        for (Ambulance a : controller.getFleet()) {
            fleetModel.addElement(String.format("%s  [%s]%s", a.getId(), statusLabel(a.getStatus()),
                    a.getAssignedPatientId() != null ? " -> " + a.getAssignedPatientId() : ""));
        }

        doctorStatusModel.clear();
        for (Doctor d : controller.getDoctors()) {
            String status = d.getStatus() == Doctor.Status.BUSY
                    ? "Treating " + d.getCurrentPatientId()
                    : "Available";
            doctorStatusModel.addElement(d.getName() + " - " + status);
        }

        logModel.clear();
        List<String> events = controller.getEventLog();
        for (String e : events) logModel.addElement(e);

        // Manage tab
        syncListPreservingSelection(rosterList, rosterModel, controller.getDoctors());
        rosterWrap.refreshVisibility();

        syncListPreservingSelection(pendingCallsList, pendingCallsModel, controller.getPendingCalls());
        pendingCallsWrap.refreshVisibility();

        java.util.List<Ambulance> idle = controller.getFleet().stream()
                .filter(a -> a.getStatus() == Ambulance.Status.IDLE)
                .collect(java.util.stream.Collectors.toList());
        syncListPreservingSelection(idleAmbulanceList, idleAmbulanceModel, idle);
        idleAmbulanceWrap.refreshVisibility();

        syncListPreservingSelection(waitingForDoctorList, waitingForDoctorModel, controller.getWaitingRoom());
        waitingForDoctorWrap.refreshVisibility();

        java.util.List<Doctor> available = controller.getDoctors().stream()
                .filter(d -> d.getStatus() == Doctor.Status.AVAILABLE)
                .collect(java.util.stream.Collectors.toList());
        syncListPreservingSelection(availableDoctorList, availableDoctorModel, available);
        availableDoctorWrap.refreshVisibility();

        reportPanel.refresh();
    }

    private String statusLabel(Ambulance.Status s) {
        switch (s) {
            case TO_PATIENT: return "En route to patient";
            case TO_HOSPITAL: return "Returning to hospital";
            default: return "Idle";
        }
    }

    // ---------------------------------------------------------------
    // Cell renderers
    // ---------------------------------------------------------------

    /** Shows a colored urgency badge per patient; critical (level 1) patients pulse. */
    private static class PatientCellRenderer extends JLabel implements ListCellRenderer<Patient> {
        @Override
        public Component getListCellRendererComponent(JList<? extends Patient> list, Patient p, int index,
                                                        boolean isSelected, boolean cellHasFocus) {
            setOpaque(true);
            setFont(Theme.FONT_BODY);
            setBorder(new EmptyBorder(3, 6, 3, 6));
            setForeground(Theme.TEXT_LIGHT);
            setText("  " + p.getId() + " - " + p.getName() + "  (" + p.urgencyLabel() + ")");
            setIcon(new ColorDot(Theme.urgencyColor(p.getUrgencyLevel())));

            if (p.getUrgencyLevel() == 1) {
                double phase = (System.currentTimeMillis() % 1000) / 1000.0;
                int alpha = (int) (60 + 60 * Math.abs(Math.sin(phase * Math.PI)));
                setBackground(new Color(Theme.ACCENT_RED.getRed(), Theme.ACCENT_RED.getGreen(), Theme.ACCENT_RED.getBlue(), alpha));
            } else {
                setBackground(isSelected ? Theme.PANEL_DARK_ALT : Theme.PANEL_DARK);
            }
            return this;
        }
    }

    private static class DoctorCellRenderer extends JLabel implements ListCellRenderer<Doctor> {
        @Override
        public Component getListCellRendererComponent(JList<? extends Doctor> list, Doctor d, int index,
                                                        boolean isSelected, boolean cellHasFocus) {
            setOpaque(true);
            setFont(Theme.FONT_BODY);
            setBorder(new EmptyBorder(3, 6, 3, 6));
            setForeground(Theme.TEXT_LIGHT);
            setBackground(isSelected ? Theme.PANEL_DARK_ALT : Theme.PANEL_DARK);
            String status = d.getStatus() == Doctor.Status.AVAILABLE ? "Available" : "Busy";
            setText("  " + d.getName() + " (" + d.getSpecialty() + ") - " + status);
            setIcon(new ColorDot(d.getStatus() == Doctor.Status.AVAILABLE ? Theme.ACCENT_GREEN : Theme.ACCENT_GREY));
            return this;
        }
    }

    private static class AmbulanceCellRenderer extends JLabel implements ListCellRenderer<Ambulance> {
        @Override
        public Component getListCellRendererComponent(JList<? extends Ambulance> list, Ambulance a, int index,
                                                        boolean isSelected, boolean cellHasFocus) {
            setOpaque(true);
            setFont(Theme.FONT_BODY);
            setBorder(new EmptyBorder(3, 6, 3, 6));
            setForeground(Theme.TEXT_LIGHT);
            setBackground(isSelected ? Theme.PANEL_DARK_ALT : Theme.PANEL_DARK);
            setText("  " + a.getId() + "  (node " + a.getCurrentNodeId() + ")");
            setIcon(new ColorDot(Theme.AMBULANCE_IDLE));
            return this;
        }
    }

    private static class ColorDot implements Icon {
        private final Color color;
        ColorDot(Color color) { this.color = color; }
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillOval(x, y + 3, 10, 10);
        }
        @Override public int getIconWidth() { return 14; }
        @Override public int getIconHeight() { return 14; }
    }
}
