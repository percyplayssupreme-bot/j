package dev.lvstrng.venum.module.modules.render;

import dev.lvstrng.venum.module.Category;
import dev.lvstrng.venum.module.Module;
import dev.lvstrng.venum.utils.EncryptedString;

public final class NoBounce extends Module {
	public NoBounce() {
		super(EncryptedString.of("No Bounce"),
				EncryptedString.of("Removes the crystal bounce"),
				-1,
				Category.RENDER);
	}
}
