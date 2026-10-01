package com.nangclient;

import net.minecraft.client.gui.GuiGraphics;

/** Colors and small drawing helpers used by the whole client. */
public final class Theme {
	public static final int BG = 0xF00B0F1A;
	public static final int SIDEBAR = 0xFF0F1524;
	public static final int ROW = 0xFF141B2D;
	public static final int ROW_HOVER = 0xFF1B2540;
	public static final int ACCENT = 0xFF3B82F6;
	public static final int ACCENT_LIGHT = 0xFF60A5FA;
	public static final int TEXT = 0xFFE5EDFF;
	public static final int MUTED = 0xFF8A97B5;
	public static final int OFF = 0xFF2A3350;

	private Theme() {}

	/** A filled rectangle with slightly cut corners (looks rounded). */
	public static void rounded(GuiGraphics g, int x, int y, int w, int h, int color) {
		g.fill(x + 1, y, x + w - 1, y + 1, color);
		g.fill(x, y + 1, x + w, y + h - 1, color);
		g.fill(x + 1, y + h - 1, x + w - 1, y + h, color);
	}

	public static void outline(GuiGraphics g, int x, int y, int w, int h, int color) {
		g.fill(x, y, x + w, y + 1, color);
		g.fill(x, y + h - 1, x + w, y + h, color);
		g.fill(x, y, x + 1, y + h, color);
		g.fill(x + w - 1, y, x + w, y + h, color);
	}

	public static boolean inside(int mx, int my, int x, int y, int w, int h) {
		return mx >= x && mx < x + w && my >= y && my < y + h;
	}

	public static int withAlpha(int rgb, int alpha) {
		return (alpha << 24) | (rgb & 0xFFFFFF);
	}

	public static int accent(String name) {
		return switch (name) {
			case "Cyan" -> 0xFF22D3EE;
			case "Purple" -> 0xFFA78BFA;
			case "Green" -> 0xFF34D399;
			case "Red" -> 0xFFF87171;
			case "White" -> 0xFFFFFFFF;
			default -> ACCENT;
		};
	}

	/** A 26x12 on/off switch. */
	public static void toggle(GuiGraphics g, int x, int y, boolean on) {
		rounded(g, x, y, 26, 12, on ? ACCENT : OFF);
		rounded(g, on ? x + 16 : x + 2, y + 2, 8, 8, 0xFFFFFFFF);
	}
}
