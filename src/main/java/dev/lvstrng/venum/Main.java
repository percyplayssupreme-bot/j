package dev.lvstrng.venum;

import net.fabricmc.api.ModInitializer;

import java.io.IOException;

public final class Main implements ModInitializer {
	@Override
	public void onInitialize() {
		try {
			new Venum();
		} catch (InterruptedException | IOException ignored) {}
	}
}
