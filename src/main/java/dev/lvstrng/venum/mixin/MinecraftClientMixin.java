package dev.lvstrng.venum.mixin;

import dev.lvstrng.venum.Venum;
import dev.lvstrng.venum.event.EventManager;
import dev.lvstrng.venum.event.events.*;
import dev.lvstrng.venum.module.modules.misc.FastPlace;
import dev.lvstrng.venum.utils.MouseSimulation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Shadow
	@Nullable
	public ClientWorld world;

	@Shadow
	@Final
	private Window window;

	@Shadow
	private int itemUseCooldown;

	@Inject(method = "tick", at = @At("HEAD"))
	private void onTick(CallbackInfo ci) {
		if (world != null) {
			TickListener.TickEvent event = new TickListener.TickEvent();

			EventManager.fire(event);
		}
	}

	@Inject(method = "onResolutionChanged", at = @At("HEAD"))
	private void onResolutionChanged(CallbackInfo ci) {
		EventManager.fire(new ResolutionListener.ResolutionEvent(this.window));
	}

	@Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
	private void onItemUse(CallbackInfo ci) {
		ItemUseListener.ItemUseEvent event = new ItemUseListener.ItemUseEvent();

		EventManager.fire(event);
		if (event.isCancelled()) ci.cancel();

		if (MouseSimulation.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
			MouseSimulation.mouseButtons.put(GLFW.GLFW_MOUSE_BUTTON_RIGHT, false);
			ci.cancel();
		}
	}

	@Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
	private void onAttack(CallbackInfoReturnable<Boolean> cir) {
		AttackListener.AttackEvent event = new AttackListener.AttackEvent();

		EventManager.fire(event);
		if (event.isCancelled()) cir.setReturnValue(false);

		if (MouseSimulation.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_1)) {
			MouseSimulation.mouseButtons.put(GLFW.GLFW_MOUSE_BUTTON_1, false);
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
	private void onBlockBreaking(boolean breaking, CallbackInfo ci) {
		BlockBreakingListener.BlockBreakingEvent event = new BlockBreakingListener.BlockBreakingEvent();

		EventManager.fire(event);
		if (event.isCancelled()) ci.cancel();

		if (MouseSimulation.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_1)) {
			MouseSimulation.mouseButtons.put(GLFW.GLFW_MOUSE_BUTTON_1, false);
			ci.cancel();
		}
	}

	@Inject(method = "stop", at = @At("HEAD"))
	private void onClose(CallbackInfo ci) {
		Venum.INSTANCE.getProfileManager().saveProfile();
	}

	@Redirect(method = "doItemUse",
			at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;itemUseCooldown:I", opcode = Opcodes.GETFIELD, ordinal = 0))
	private int redirectItemUseCooldown(MinecraftClient mc) {
		return Venum.INSTANCE.getModuleManager().getModule(FastPlace.class).isEnabled() ? 0 : this.itemUseCooldown;
	}
}
