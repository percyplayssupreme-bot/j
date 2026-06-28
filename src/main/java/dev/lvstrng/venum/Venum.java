package dev.lvstrng.venum;

import dev.lvstrng.venum.event.EventManager;
import dev.lvstrng.venum.gui.ClickGui;
import dev.lvstrng.venum.managers.FriendManager;
import dev.lvstrng.venum.module.ModuleManager;
import dev.lvstrng.venum.managers.ProfileManager;
import dev.lvstrng.venum.utils.rotation.RotatorManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import java.io.File;
import java.io.IOException;
import java.net.*;

@SuppressWarnings("all")
public final class Venum {
	public RotatorManager rotatorManager;
	public ProfileManager profileManager;
	public ModuleManager moduleManager;
	public EventManager eventManager;
	public FriendManager friendManager;
	public static MinecraftClient mc;
	public String version = " b1.3";
	public static boolean BETA; //this was for beta kids but ablue never made it a reality, and you basically paid extra 10 bucks for nothing while ablue spent it all on war thunder to buy pre-historic tanks and estrogen 🤡🤡🤡
	public static Venum INSTANCE;
	public boolean guiInitialized;
	public ClickGui clickGui;
	public Screen previousScreen = null;
	public long lastModified;
	public File argonJar;

	public Venum() throws InterruptedException, IOException {
		INSTANCE = this;
		this.eventManager = new EventManager();
		this.moduleManager = new ModuleManager();
		this.clickGui = new ClickGui();
		this.rotatorManager = new RotatorManager();
		this.profileManager = new ProfileManager();
		this.friendManager = new FriendManager();

		this.getProfileManager().loadProfile();
		this.setLastModified();

		this.guiInitialized = false;
		mc = MinecraftClient.getInstance();
	}

	public ProfileManager getProfileManager() {
		return profileManager;
	}

	public ModuleManager getModuleManager() {
		return moduleManager;
	}

	public FriendManager getFriendManager() {
		return friendManager;
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	public ClickGui getClickGui() {
		return clickGui;
	}

	public void resetModifiedDate() {
		this.argonJar.setLastModified(lastModified);
	}

	public String getVersion() {
		return version;
	}

	public void setLastModified() {
		try {
			this.argonJar = new File(Venum.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			// Comment out when debugging
			this.lastModified = argonJar.lastModified();
		} catch (URISyntaxException ignored) {}
	}
}