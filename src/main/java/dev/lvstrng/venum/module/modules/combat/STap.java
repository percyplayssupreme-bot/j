package dev.lvstrng.venum.module.modules.combat;

import dev.lvstrng.venum.event.events.AttackListener;
import dev.lvstrng.venum.event.events.TickListener;
import dev.lvstrng.venum.module.Category;
import dev.lvstrng.venum.module.Module;
import dev.lvstrng.venum.module.setting.NumberSetting;
import dev.lvstrng.venum.utils.EncryptedString;
import dev.lvstrng.venum.utils.TimerUtils;

public final class STap extends Module implements AttackListener, TickListener {
	private final NumberSetting holdTime = new NumberSetting(EncryptedString.of("Hold Time"), 1, 200, 60, 1);

	private final TimerUtils timer = new TimerUtils();
	private boolean holding = false;

	public STap() {
		super(EncryptedString.of("S-Tap"),
				EncryptedString.of("Holds the S key for a set duration after hitting an enemy to reduce knockback taken"),
				-1,
				Category.COMBAT);
		addSettings(holdTime);
	}

	@Override
	public void onEnable() {
		eventManager.add(AttackListener.class, this);
		eventManager.add(TickListener.class, this);
		holding = false;
		super.onEnable();
	}

	@Override
	public void onDisable() {
		eventManager.remove(AttackListener.class, this);
		eventManager.remove(TickListener.class, this);
		mc.options.backKey.setPressed(false);
		holding = false;
		super.onDisable();
	}

	@Override
	public void onAttack(AttackEvent event) {
		mc.options.backKey.setPressed(true);
		timer.reset();
		holding = true;
	}

	@Override
	public void onTick() {
		if (!holding) return;

		if (timer.hasReached(holdTime.getValue())) {
			mc.options.backKey.setPressed(false);
			holding = false;
		}
	}
}
