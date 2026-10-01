package com.nangclient;

import com.nangclient.module.Module;
import com.nangclient.module.ModuleManager;
import com.nangclient.module.Setting;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** The NangClient module menu: tabs, module list and a settings panel. Everything is clickable. */
public class ClientMenuScreen extends Screen {
	private final Screen parent;
	private Module.Category tab = Module.Category.HUD;
	private Module selected;
	private boolean wasDown;

	public ClientMenuScreen(Screen parent) {
		super(Component.literal("NangClient"));
		this.parent = parent;
		this.wasDown = Minecraft.getInstance().mouseHandler.isLeftPressed();
	}

	@Override
	public void onClose() {
		Config.save();
		Minecraft.getInstance().setScreen(parent);
	}

	private static void centered(GuiGraphics g, Font font, String s, int x, int y, int w, int color) {
		g.drawString(font, s, x + (w - font.width(s)) / 2, y, color);
	}

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
		Minecraft mc = Minecraft.getInstance();
		Font font = mc.font;
		boolean down = mc.mouseHandler.isLeftPressed();
		boolean click = down && !wasDown;
		wasDown = down;

		g.fill(0, 0, this.width, this.height, 0x99000000);

		int pw = Math.min(460, this.width - 16);
		int ph = Math.min(264, this.height - 16);
		int px = (this.width - pw) / 2;
		int py = (this.height - ph) / 2;

		Theme.rounded(g, px, py, pw, ph, Theme.BG);
		g.fill(px + 1, py, px + pw - 1, py + 2, Theme.ACCENT);

		// Header
		int nangW = font.width("Nang");
		g.drawString(font, "Nang", px + 12, py + 12, Theme.ACCENT_LIGHT);
		g.drawString(font, "Client", px + 12 + nangW, py + 12, Theme.TEXT);
		g.drawString(font, "by NangDev", px + 12 + nangW + font.width("Client") + 6, py + 12, Theme.MUTED);

		// Edit HUD button
		int bx = px + pw - 86;
		int by = py + 9;
		boolean hovEdit = Theme.inside(mouseX, mouseY, bx, by, 78, 16);
		Theme.rounded(g, bx, by, 78, 16, hovEdit ? Theme.ACCENT : Theme.ROW_HOVER);
		centered(g, font, "Edit HUD", bx, by + 4, 78, Theme.TEXT);
		if (hovEdit && click) {
			mc.setScreen(new HudEditorScreen(this));
			return;
		}

		// Sidebar tabs
		int sbX = px + 6;
		int sbY = py + 34;
		int sbW = 82;
		int i = 0;
		for (Module.Category c : Module.Category.values()) {
			int ty = sbY + i * 22;
			boolean sel = c == tab;
			boolean hov = Theme.inside(mouseX, mouseY, sbX, ty, sbW, 18);
			if (sel) {
				Theme.rounded(g, sbX, ty, sbW, 18, Theme.withAlpha(0x3B82F6, 0x55));
				g.fill(sbX, ty + 2, sbX + 2, ty + 16, Theme.ACCENT);
			} else if (hov) {
				Theme.rounded(g, sbX, ty, sbW, 18, Theme.ROW);
			}
			g.drawString(font, c.label, sbX + 9, ty + 5, sel ? Theme.TEXT : Theme.MUTED);
			if (hov && click) {
				tab = c;
				selected = null;
				click = false;
			}
			i++;
		}

		// Module list
		List<Module> mods = ModuleManager.byCategory(tab);
		if (selected == null || selected.category != tab) {
			selected = mods.isEmpty() ? null : mods.get(0);
		}
		int lx = px + 94;
		int ly = py + 34;
		int lw = Math.min(170, (pw - 94 - 18) / 2);
		int row = 0;
		for (Module m : mods) {
			int ry = ly + row * 24;
			boolean hov = Theme.inside(mouseX, mouseY, lx, ry, lw, 22);
			boolean sel = m == selected;
			Theme.rounded(g, lx, ry, lw, 22, sel || hov ? Theme.ROW_HOVER : Theme.ROW);
			if (sel) g.fill(lx, ry + 3, lx + 2, ry + 19, Theme.ACCENT);
			g.drawString(font, m.name, lx + 9, ry + 7, m.enabled ? Theme.TEXT : Theme.MUTED);
			int swx = lx + lw - 34;
			Theme.toggle(g, swx, ry + 5, m.enabled);
			if (hov && click) {
				if (Theme.inside(mouseX, mouseY, swx - 3, ry, 37, 22)) {
					m.toggle();
					Config.save();
				} else {
					selected = m;
				}
				click = false;
			}
			row++;
		}

		// Settings panel
		int sx = lx + lw + 8;
		int sw = px + pw - 8 - sx;
		int sh = ph - 42;
		Theme.rounded(g, sx, ly, sw, sh, Theme.SIDEBAR);
		if (selected != null) {
			g.drawString(font, selected.name, sx + 10, ly + 10, Theme.ACCENT_LIGHT);
			String[] lines = selected.description.split("\\|");
			for (int d = 0; d < lines.length; d++) {
				g.drawString(font, lines[d], sx + 10, ly + 24 + d * 10, Theme.MUTED);
			}
			int baseY = ly + 24 + lines.length * 10 + 8;
			if (selected.settings.isEmpty()) {
				g.drawString(font, "No settings for this module.", sx + 10, baseY + 4, Theme.MUTED);
			}
			int k = 0;
			for (Setting s : selected.settings) {
				int ry = baseY + k * 22;
				g.drawString(font, s.name, sx + 10, ry + 5, Theme.TEXT);
				if (s.isToggle()) {
					int tx = sx + sw - 36;
					Theme.toggle(g, tx, ry + 3, s.on());
					if (click && Theme.inside(mouseX, mouseY, sx + 6, ry, sw - 12, 18)) {
						s.next();
						Config.save();
						click = false;
					}
				} else {
					int cw = 64;
					int cx = sx + sw - cw - 8;
					boolean hov = Theme.inside(mouseX, mouseY, cx, ry, cw, 18);
					Theme.rounded(g, cx, ry, cw, 18, hov ? Theme.ACCENT : Theme.ROW_HOVER);
					String v = s.value();
					int textX = cx + (cw - font.width(v)) / 2;
					if (s.name.equals("Color")) {
						Theme.rounded(g, cx + 6, ry + 5, 8, 8, Theme.accent(v));
						textX = cx + 8 + (cw - 8 - font.width(v)) / 2;
					}
					g.drawString(font, v, textX, ry + 5, Theme.TEXT);
					if (hov && click) {
						s.next();
						Config.save();
						click = false;
					}
				}
				k++;
			}
		}

		g.drawString(font, "Click a module to edit it  |  Esc to close", px + 12, py + ph - 13, Theme.MUTED);
	}
}
