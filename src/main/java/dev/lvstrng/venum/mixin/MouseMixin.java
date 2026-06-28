package dev.lvstrng.venum.mixin;

import dev.lvstrng.venum.Venum;
import dev.lvstrng.venum.event.EventManager;
import dev.lvstrng.venum.event.events.ButtonListener;
import dev.lvstrng.venum.event.events.MouseMoveListener;
import dev.lvstrng.venum.event.events.MouseUpdateListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseMixin {
	@Shadow @Final private MinecraftClient client;
	@Shadow public abstract double getX();
	@Shadow public abstract double getY();

	@Unique private double venum$lastMouseX;
	@Unique private double venum$lastMouseY;
	@Unique private boolean venum$initialized;
	@Unique private final int[] venum$buttonStates = new int[GLFW.GLFW_MOUSE_BUTTON_LAST + 1];

	@Inject(method = "tick", at = @At("TAIL"))
	private void onMouseUpdate(CallbackInfo ci) {
		EventManager.fire(new MouseUpdateListener.MouseUpdateEvent());
		long window = client.getWindow().getHandle();
		double x = getX();
		double y = getY();

		if (!venum$initialized) {
			venum$initialized = true;
			venum$lastMouseX = x;
			venum$lastMouseY = y;

			for (int button = 0; button < venum$buttonStates.length; button++) {
				venum$buttonStates[button] = GLFW.glfwGetMouseButton(window, button);
			}
			return;
		}

		if (x != venum$lastMouseX || y != venum$lastMouseY) {
			venum$lastMouseX = x;
			venum$lastMouseY = y;
			EventManager.fire(new MouseMoveListener.MouseMoveEvent(window, x, y));
		}

		for (int button = 0; button < venum$buttonStates.length; button++) {
			int state = GLFW.glfwGetMouseButton(window, button);
			if (state != venum$buttonStates[button]) {
				venum$buttonStates[button] = state;
				EventManager.fire(new ButtonListener.ButtonEvent(button, window, state));
			}
		}
	}
}
