package com.nangclient;

import com.nangclient.module.HudModule;
import com.nangclient.module.ModuleManager;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Drag the HUD boxes anywhere you like. */
public class HudEditorScreen extends Screen {
	private final Screen parent;
	private HudModule dragging;
	private int offX;
	private int offY;
	private boolean wasDown;

	public HudEditorScreen(Screen parent) {
		super(Component.literal("Edit HUD"));
		this.parent = parent;
		this.wasDown = Minecraft.getInstance().mouseHandler.isLeftPressed();
	}

	@Override
	public void onClose() {
		Config.save();
		Minecraft.getInstance().setScreen(parent);
	}

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
		Minecraft mc = Minecraft.getInstance();
		Font font = mc.font;
		boolean down = mc.mouseHandler.isLeftPressed();
		List<HudModule> mods = ModuleManager.hud();

		g.fill(0, 0, this.width, this.height, 0x66000000);

		if (down && !wasDown) {
			dragging = null;
			for (int i = mods.size() - 1; i >= 0; i--) {
				HudModule m = mods.get(i);
				if (m.enabled && m.contains(mc, mouseX, mouseY)) {
					dragging = m;
					offX = mouseX - m.x;
					offY = mouseY - m.y;
					break;
				}
			}
		}
		if (down && dragging != null) {
			int maxX = Math.max(0, this.width - dragging.screenWidth(mc));
			int maxY = Math.max(0, this.height - dragging.screenHeight(mc));
			dragging.x = Math.max(0, Math.min(maxX, mouseX - offX));
			dragging.y = Math.max(0, Math.min(maxY, mouseY - offY));
		}
		if (!down && dragging != null) {
			dragging = null;
			Config.save();
		}
		wasDown = down;

		for (HudModule m : mods) {
			if (m.enabled) m.render(g, mc, true);
		}

		String msg = "Drag the boxes to move them  -  Esc to finish";
		g.drawString(font, msg, (this.width - font.width(msg)) / 2, 10, Theme.TEXT);
	}
}
