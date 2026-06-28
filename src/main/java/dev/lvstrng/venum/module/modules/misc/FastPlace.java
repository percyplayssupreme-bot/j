package dev.lvstrng.venum.module.modules.misc;

import dev.lvstrng.venum.module.Category;
import dev.lvstrng.venum.module.Module;
import dev.lvstrng.venum.utils.EncryptedString;

public final class FastPlace extends Module {
	public FastPlace() {
		super(EncryptedString.of("Fast Place"),
				EncryptedString.of("Removes the delay when placing blocks"),
				-1,
				Category.MISC);
	}
}
