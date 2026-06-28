package dev.lvstrng.venum.imixin;

import net.minecraft.util.math.Vec3d;

public interface IExplosion {
	void set(Vec3d pos, float power, boolean createFire);
}
