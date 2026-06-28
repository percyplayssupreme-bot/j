package dev.lvstrng.venum.module.modules.misc;

import dev.lvstrng.venum.event.events.TickListener;
import dev.lvstrng.venum.module.Category;
import dev.lvstrng.venum.module.Module;
import dev.lvstrng.venum.utils.EncryptedString;

public final class Sprint extends Module implements TickListener {
    public Sprint() {
        super(EncryptedString.of("Sprint"), EncryptedString.of("Keeps you sprinting at all times"), -1, Category.MISC);
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
        mc.player.setSprinting(mc.player.input.hasForwardMovement());
    }
}
