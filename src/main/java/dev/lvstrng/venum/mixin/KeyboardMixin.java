package dev.lvstrng.venum.mixin;

import dev.lvstrng.venum.Venum;
import dev.lvstrng.venum.event.EventManager;
import dev.lvstrng.venum.event.events.ButtonListener;
import dev.lvstrng.venum.module.Module;
import dev.lvstrng.venum.module.modules.client.SelfDestruct;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyInput;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(method = "onKey", at = @At("HEAD"))
	private void onPress(long window, int action, KeyInput input, CallbackInfo ci) {
		EventManager.fire(new ButtonListener.ButtonEvent(input.key(), window, action));
	}
}
