package com.nangclient.module;

import com.nangclient.Keys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/** Gameplay, visual and performance modules (no on-screen box). */
public final class Modules {
	private Modules() {}

	public static final class ToggleSprint extends Module {
		public ToggleSprint() {
			super("Toggle Sprint", "Keeps you sprinting|while you walk forward.", Category.GAMEPLAY, true);
		}

		@Override
		public void onTick(Minecraft mc) {
			if (mc.player == null) return;
			if (mc.options.keyUp.isDown()) {
				mc.options.keySprint.setDown(true);
			}
		}

		@Override
		public void onDisable() {
			Minecraft.getInstance().options.keySprint.setDown(false);
		}
	}

	public static final class Zoom extends Module {
		private static final int[] FOVS = {30, 45, 60};
		private final Setting level = add(new Setting("Zoom Level", 0, "Strong", "Medium", "Light"));
		private boolean zooming;
		private int savedFov;

		public Zoom() {
			super("Zoom", "Hold C to zoom in.|(Change the key in Controls)", Category.GAMEPLAY, true);
		}

		@Override
		public void onTick(Minecraft mc) {
			boolean held = Keys.zoom != null && Keys.zoom.isDown() && mc.screen == null;
			if (held && mc.player != null) {
				if (!zooming) {
					savedFov = mc.options.fov().get();
					zooming = true;
				}
				int target = FOVS[level.index];
				if (mc.options.fov().get() != target) {
					mc.options.fov().set(target);
				}
			} else if (zooming) {
				restore(mc);
			}
		}

		@Override
		public void onDisable() {
			if (zooming) restore(Minecraft.getInstance());
		}

		private void restore(Minecraft mc) {
			mc.options.fov().set(savedFov);
			zooming = false;
		}
	}

	public static final class Fullbright extends Module {
		private boolean applied;

		public Fullbright() {
			super("Fullbright", "See clearly in dark|places.", Category.VISUAL, false);
		}

		@Override
		public void onTick(Minecraft mc) {
			if (mc.player == null) return;
			MobEffectInstance current = mc.player.getEffect(MobEffects.NIGHT_VISION);
			if (current == null || current.getDuration() < 300) {
				mc.player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 1200, 0, false, false, false));
				applied = true;
			}
		}

		@Override
		public void onDisable() {
			Minecraft mc = Minecraft.getInstance();
			if (applied && mc.player != null) {
				mc.player.removeEffect(MobEffects.NIGHT_VISION);
			}
			applied = false;
		}
	}

	/** Lowers a few heavy game settings to get more FPS. Turning it off restores them. */
	public static final class FpsBoost extends Module {
		private static final int[] DISTANCES = {6, 8, 10, 12};
		private final Setting distance = add(new Setting("Distance", 1, "6", "8", "10", "12"));
		private boolean applied;
		private int oldRender;
		private int oldSim;
		private boolean oldShadows;
		private ParticleStatus oldParticles;

		public FpsBoost() {
			super("FPS Boost", "Lowers render distance,|shadows and particles.", Category.PERFORMANCE, false);
		}

		@Override
		public void onTick(Minecraft mc) {
			var o = mc.options;
			if (!applied) {
				oldRender = o.renderDistance().get();
				oldSim = o.simulationDistance().get();
				oldShadows = o.entityShadows().get();
				oldParticles = o.particles().get();
				applied = true;
			}
			int target = DISTANCES[distance.index];
			if (o.renderDistance().get() != target) o.renderDistance().set(target);
			if (o.simulationDistance().get() != 6) o.simulationDistance().set(6);
			if (o.entityShadows().get()) o.entityShadows().set(false);
			if (o.particles().get() != ParticleStatus.MINIMAL) o.particles().set(ParticleStatus.MINIMAL);
		}

		@Override
		public void onDisable() {
			if (!applied) return;
			var o = Minecraft.getInstance().options;
			o.renderDistance().set(oldRender);
			o.simulationDistance().set(oldSim);
			o.entityShadows().set(oldShadows);
			o.particles().set(oldParticles);
			applied = false;
		}
	}
}
