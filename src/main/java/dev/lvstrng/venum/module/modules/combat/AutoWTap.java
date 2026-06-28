package dev.lvstrng.venum.module.modules.combat;

import dev.lvstrng.venum.event.events.AttackListener;
import dev.lvstrng.venum.event.events.TickListener;
import dev.lvstrng.venum.module.Category;
import dev.lvstrng.venum.module.Module;
import dev.lvstrng.venum.module.setting.BooleanSetting;
import dev.lvstrng.venum.module.setting.MinMaxSetting;
import dev.lvstrng.venum.utils.EncryptedString;
import dev.lvstrng.venum.utils.TimerUtils;

public final class AutoWTap extends Module implements AttackListener, TickListener {
	private final MinMaxSetting delay = new MinMaxSetting(EncryptedString.of("Delay"), 0, 1000, 1, 230, 270);
	private final BooleanSetting inAir = new BooleanSetting(EncryptedString.of("In Air"), false)
			.setDescription(EncryptedString.of("Whether it should W tap in air"));

	private final TimerUtils timer = new TimerUtils();
	private boolean tapping = false;
	private int currentDelay;

	public AutoWTap() {
		super(EncryptedString.of("Auto WTap"),
				EncryptedString.of("Automatically W Taps for you so the opponent takes more knockback"),
				-1,
				Category.COMBAT);
		addSettings(delay, inAir);
	}

	@Override
	public void onEnable() {
		eventManager.add(AttackListener.class, this);
		eventManager.add(TickListener.class, this);
		tapping = false;
		currentDelay = delay.getRandomValueInt();
		super.onEnable();
	}

	@Override
	public void onDisable() {
		eventManager.remove(AttackListener.class, this);
		eventManager.remove(TickListener.class, this);
		if (mc.player != null && tapping)
			mc.player.setSprinting(true);
		tapping = false;
		super.onDisable();
	}

	@Override
	public void onAttack(AttackEvent event) {
		if (mc.player == null) return;
		if (!inAir.getValue() && !mc.player.isOnGround()) return;
		if (!mc.player.isSprinting()) return;

		mc.player.setSprinting(false);
		timer.reset();
		tapping = true;
		currentDelay = delay.getRandomValueInt();
	}

	@Override
	public void onTick() {
		if (!tapping || mc.player == null) return;

		if (timer.hasReached(currentDelay)) {
			mc.player.setSprinting(true);
			tapping = false;
		}
	}
}
