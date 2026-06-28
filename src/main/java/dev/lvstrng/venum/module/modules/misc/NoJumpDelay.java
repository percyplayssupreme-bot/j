package dev.lvstrng.venum.module.modules.misc;

import dev.lvstrng.venum.event.events.TickListener;
import dev.lvstrng.venum.module.Category;
import dev.lvstrng.venum.module.Module;
import dev.lvstrng.venum.utils.EncryptedString;
import org.lwjgl.glfw.GLFW;

public final class NoJumpDelay extends Module implements TickListener {
	public NoJumpDelay() {
		super(EncryptedString.of("No Jump Delay"),
				EncryptedString.of("Lets you jump faster, removing the delay"),
				-1,
				Category.MISC);
	}

	@Override
	public void onEnable() {
		eventManager.add(TickListener.class, this);
		super.onEnable();
	}

	@Override
	public void onDisable() {
		eventManager.remove(TickListener.class, this);
		super.onDisable();
	}

	@Override
	public void onTick() {
		if (mc.currentScreen != null)
			return;

		if (!mc.player.isOnGround())
			return;

		if (GLFW.glfwGetKey(mc.getWindow().getHandle(), GLFW.GLFW_KEY_SPACE) != GLFW.GLFW_PRESS)
			return;

		mc.options.jumpKey.setPressed(false);
		mc.player.jump();
	}
}
