package com.elevate.emergency.gui;

import java.awt.*;

/**
 * Central color/font palette for the GUI, with a toggleable dark/light mode.
 * Base surface colors are mutable and reassigned by setDarkMode(); callers
 * re-read them via repaint()/refresh() after a toggle rather than caching.
 * Accent colors stay constant in both modes so status meaning doesn't shift.
 */
public final class Theme {

    public static boolean darkMode = true;

    // ---- Mutable surface colors (swap between light/dark) ----
    public static Color BG_DARK;
    public static Color PANEL_DARK;
    public static Color PANEL_DARK_ALT;
    public static Color MAP_BG;
    public static Color MAP_BG_2;
    public static Color ROAD_COLOR;
    public static Color ROAD_LABEL;
    public static Color TEXT_LIGHT;
    public static Color TEXT_MUTED;
    public static Color NODE_FILL;
    public static Color NODE_BORDER;

    // ---- Fixed accent colors (top/bottom pairs for gradients) ----
    public static final Color ACCENT_BLUE     = new Color(0x3B, 0x82, 0xF6);
    public static final Color ACCENT_BLUE_DK  = new Color(0x21, 0x5E, 0xC9);
    public static final Color ACCENT_TEAL     = new Color(0x1C, 0xC9, 0xB0);
    public static final Color ACCENT_TEAL_DK  = new Color(0x0E, 0x9E, 0x88);
    public static final Color ACCENT_PURPLE   = new Color(0x9B, 0x6C, 0xF6);
    public static final Color ACCENT_PURPLE_DK= new Color(0x74, 0x46, 0xCF);
    public static final Color ACCENT_GREEN    = new Color(0x2E, 0xD1, 0x74);
    public static final Color ACCENT_GREEN_DK = new Color(0x18, 0xA3, 0x53);
    public static final Color ACCENT_RED      = new Color(0xF4, 0x5B, 0x69);
    public static final Color ACCENT_RED_DK   = new Color(0xC1, 0x2A, 0x3A);
    public static final Color ACCENT_GREY     = new Color(0x6B, 0x74, 0x85);
    public static final Color ACCENT_GREY_DK  = new Color(0x4C, 0x54, 0x62);

    public static final Color HOSPITAL_FILL_1 = ACCENT_RED;
    public static final Color HOSPITAL_FILL_2 = ACCENT_RED_DK;

    public static final Color AMBULANCE_IDLE         = ACCENT_GREEN;
    public static final Color AMBULANCE_TO_PATIENT   = new Color(0xF5, 0x9E, 0x0B);
    public static final Color AMBULANCE_TO_HOSPITAL  = ACCENT_BLUE;

    // Urgency 1 (critical) -> 5 (minor)
    public static final Color[] URGENCY_COLORS = {
            new Color(0xEF, 0x44, 0x44),
            new Color(0xF9, 0x73, 0x16),
            new Color(0xF5, 0x9E, 0x0B),
            new Color(0xEA, 0xB3, 0x08),
            new Color(0x84, 0xCC, 0x16)
    };

    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 19);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_MONO    = new Font("Consolas", Font.PLAIN, 12);

    static {
        applyDark();
    }

    public static void setDarkMode(boolean dark) {
        darkMode = dark;
        if (dark) applyDark(); else applyLight();
    }

    public static void toggle() { setDarkMode(!darkMode); }

    private static void applyDark() {
        BG_DARK        = new Color(0x14, 0x1A, 0x24);
        PANEL_DARK     = new Color(0x1E, 0x27, 0x34);
        PANEL_DARK_ALT = new Color(0x26, 0x31, 0x41);
        MAP_BG         = new Color(0x22, 0x2C, 0x3B);
        MAP_BG_2       = new Color(0x18, 0x20, 0x2C);
        ROAD_COLOR     = new Color(0x47, 0x54, 0x67);
        ROAD_LABEL     = new Color(0x8A, 0x93, 0xA1);
        TEXT_LIGHT     = new Color(0xEC, 0xF0, 0xF5);
        TEXT_MUTED     = new Color(0x9A, 0xA5, 0xB4);
        NODE_FILL      = new Color(0x2E, 0x39, 0x4B);
        NODE_BORDER    = new Color(0x6B, 0x7A, 0x90);
    }

    private static void applyLight() {
        BG_DARK        = new Color(0xEC, 0xF0, 0xF6);
        PANEL_DARK     = new Color(0xFF, 0xFF, 0xFF);
        PANEL_DARK_ALT = new Color(0xF1, 0xF4, 0xF9);
        MAP_BG         = new Color(0xFB, 0xFC, 0xFE);
        MAP_BG_2       = new Color(0xE8, 0xED, 0xF4);
        ROAD_COLOR     = new Color(0xC7, 0xCE, 0xD8);
        ROAD_LABEL     = new Color(0x8A, 0x93, 0xA1);
        TEXT_LIGHT     = new Color(0x1E, 0x27, 0x34);
        TEXT_MUTED     = new Color(0x5B, 0x66, 0x76);
        NODE_FILL      = new Color(0xFF, 0xFF, 0xFF);
        NODE_BORDER    = new Color(0x9A, 0xA5, 0xB4);
    }

    public static Color urgencyColor(int level) {
        int idx = Math.max(1, Math.min(5, level)) - 1;
        return URGENCY_COLORS[idx];
    }

    private Theme() {}
}
