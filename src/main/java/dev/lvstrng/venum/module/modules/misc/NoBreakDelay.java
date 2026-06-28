package dev.lvstrng.venum.module.modules.misc;

import dev.lvstrng.venum.module.Category;
import dev.lvstrng.venum.module.Module;
import dev.lvstrng.venum.utils.EncryptedString;

public final class NoBreakDelay extends Module {
	public NoBreakDelay() {
		super(EncryptedString.of("No Break Delay"),
				EncryptedString.of("Removes the break delay from mining blocks"),
				-1,
				Category.MISC);
	}
}
