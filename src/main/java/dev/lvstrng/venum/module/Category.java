package dev.lvstrng.venum.module;

import dev.lvstrng.venum.utils.EncryptedString;

public enum Category {
	COMBAT(EncryptedString.of("Combat")),
	MISC(EncryptedString.of("Misc")),
	RENDER(EncryptedString.of("Render")),
	CLIENT(EncryptedString.of("Client"));
	public final CharSequence name;

	Category(CharSequence name) {
		this.name = name;
	}
}
