package dev.lvstrng.venum.module.modules.misc;

import dev.lvstrng.venum.module.Category;
import dev.lvstrng.venum.module.Module;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class PearlCatch extends Module {
    private int stage = 0;
    private long throwTime = 0;
    private int previousSlot = -1;
    private final int delayMs = 150; // configurable value placeholder

    public PearlCatch() { super("PearlCatch", Category.MISC); }

    @Override public void onEnable() { stage = 0; previousSlot = mc.player != null ? mc.player.getInventory().selectedSlot : -1; }

    @Override public void onUpdate() {
        if (mc.player == null || mc.world == null) return;
        float current = mc.player.getPitch();
        mc.player.setPitch(Math.max(-90f, current - 15f)); // smooth rotation
        if (mc.player.getPitch() > -89f) return;

        if (stage == 0) {
            int pearlSlot = findItem(Items.ENDER_PEARL);
            if (pearlSlot == -1) { restore(); toggle(); return; }
            mc.player.getInventory().selectedSlot = pearlSlot;
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            throwTime = System.currentTimeMillis(); stage = 1;
        }

        if (stage == 1 && System.currentTimeMillis() - throwTime >= delayMs) {
            int windSlot = findItem(Items.WIND_CHARGE);
            if (windSlot != -1) { mc.player.getInventory().selectedSlot = windSlot; mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND); }
            restore(); toggle();
        }
    }
    private void restore(){ if(previousSlot != -1 && mc.player != null) mc.player.getInventory().selectedSlot = previousSlot; }
    private int findItem(net.minecraft.item.Item item){ for(int i=0;i<9;i++) if(mc.player.getInventory().getStack(i).isOf(item)) return i; return -1; }
}
