package dev.lvstrng.venum.module;

import dev.lvstrng.venum.Venum;
import dev.lvstrng.venum.event.events.ButtonListener;
import dev.lvstrng.venum.module.modules.client.ClickGUI;
import dev.lvstrng.venum.module.modules.client.Friends;
import dev.lvstrng.venum.module.modules.client.SelfDestruct;
import dev.lvstrng.venum.module.modules.combat.*;
import dev.lvstrng.venum.module.modules.misc.*;
import dev.lvstrng.venum.module.modules.render.*;
import dev.lvstrng.venum.module.setting.KeybindSetting;
import dev.lvstrng.venum.utils.EncryptedString;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public final class ModuleManager implements ButtonListener {
	private final List<Module> modules = new ArrayList<>();

	public ModuleManager() {
		addModules();
		addKeybinds();
	}

	public void addModules() {
		//Combat
		add(new AimAssist());
		add(new AnchorMacro());
		add(new AutoCrystal());
		add(new AutoDoubleHand());
		add(new AutoHitCrystal());
		add(new AutoInventoryTotem());
		add(new TriggerBot());
		add(new AutoPot());
		add(new AutoPotRefill());
		add(new AutoWTap());
		add(new STap());
		add(new CrystalOptimizer());
		add(new DoubleAnchor());
		add(new HoverTotem());
		add(new NoMissDelay());
		add(new ShieldDisabler());
		add(new TotemOffhand());
		add(new AutoJumpReset());
		add(new AutoMace());

		//Misc
		add(new Prevent());
		add(new AutoXP());
		add(new NoJumpDelay());
		add(new PingSpoof());
		add(new FakeLag());
		add(new AutoClicker());
		add(new KeyPearl());
		add(new NoBreakDelay());
		add(new FastPlace());
		add(new Freecam());
		add(new PackSpoof());
		add(new Sprint());
		add(new PearlCatch());

		//Render
		add(new HUD());
		add(new NoBounce());
		add(new PlayerESP());
		add(new StorageEsp());
		add(new OreESP());
		add(new TargetHud());

		//Client
		add(new ClickGUI());
		add(new Friends());
		add(new SelfDestruct());
	}

	public List<Module> getEnabledModules() {
		return modules.stream()
				.filter(Module::isEnabled)
				.toList();
	}


	public List<Module> getModules() {
		return modules;
	}

	public void addKeybinds() {
		Venum.INSTANCE.getEventManager().add(ButtonListener.class, this);

		for (Module module : modules)
			module.addSetting(new KeybindSetting(EncryptedString.of("Keybind"), module.getKey(), true).setDescription(EncryptedString.of("Key to enabled the module")));
	}

	public List<Module> getModulesInCategory(Category category) {
		return modules.stream()
				.filter(module -> module.getCategory() == category)
				.toList();
	}

	@SuppressWarnings("unchecked")
	public <T extends Module> T getModule(Class<T> moduleClass) {
		return (T) modules.stream()
				.filter(moduleClass::isInstance)
				.findFirst()
				.orElse(null);
	}

	public void add(Module module) {
		modules.add(module);
	}

	@Override
	public void onButtonPress(ButtonEvent event) {
		if(!SelfDestruct.destruct) {
			modules.forEach(module -> {
				if(module.getKey() == event.button && event.action == GLFW.GLFW_PRESS)
					module.toggle();
			});
		}
	}
}
