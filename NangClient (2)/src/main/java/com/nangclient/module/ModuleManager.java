package com.nangclient.module;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;

public final class ModuleManager {
	private static final List<Module> MODULES = new ArrayList<>();
	private static final List<HudModule> HUD = new ArrayList<>();

	private ModuleManager() {}

	public static void init() {
		// HUD
		add(new HudModules.Fps());
		add(new HudModules.Cps());
		add(new HudModules.Coords());
		add(new HudModules.Direction());
		add(new HudModules.Ping());
		add(new HudModules.Clock());
		add(new HudModules.Memory());
		add(new HudModules.Keystrokes());
		// Gameplay / visual / performance
		add(new Modules.ToggleSprint());
		add(new Modules.Zoom());
		add(new Modules.Fullbright());
		add(new Modules.FpsBoost());
	}

	private static void add(Module m) {
		MODULES.add(m);
		if (m instanceof HudModule h) HUD.add(h);
	}

	public static List<Module> all() {
		return MODULES;
	}

	public static List<HudModule> hud() {
		return HUD;
	}

	public static List<Module> byCategory(Module.Category c) {
		List<Module> out = new ArrayList<>();
		for (Module m : MODULES) {
			if (m.category == c) out.add(m);
		}
		return out;
	}

	public static void tick(Minecraft mc) {
		for (Module m : MODULES) {
			if (m.enabled) m.onTick(mc);
		}
	}
}
