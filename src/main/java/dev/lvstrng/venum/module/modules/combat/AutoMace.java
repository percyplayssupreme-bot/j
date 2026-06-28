package dev.lvstrng.venum.module.modules.combat;

import dev.lvstrng.venum.Venum;
import dev.lvstrng.venum.event.events.HudListener;
import dev.lvstrng.venum.event.events.TickListener;
import dev.lvstrng.venum.managers.FriendManager;
import dev.lvstrng.venum.module.Category;
import dev.lvstrng.venum.module.Module;
import dev.lvstrng.venum.module.modules.client.Friends;
import dev.lvstrng.venum.module.setting.BooleanSetting;
import dev.lvstrng.venum.module.setting.NumberSetting;
import dev.lvstrng.venum.utils.*;
import dev.lvstrng.venum.utils.rotation.Rotation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * AutoMace - Automatically performs mace fall-damage attacks
 *
 * Features:
 *  - Auto-swaps to mace when falling
 *  - Rotates to nearest enemy
 *  - Attacks only when falling (so the mace gets its fall-damage bonus)
 *  - Swaps back to previous slot after attack
 *  - Friend protection
 *  - Auto shield break before swap-back
 *  - Configurable: min fall height, target range, attack delay, rotation speed, etc.
 */
public final class AutoMace extends Module implements TickListener, HudListener {

    // ── General ────────────────────────────────────────────────────────────
    private final NumberSetting targetRange = new NumberSetting(
            EncryptedString.of("Target Range"), 1, 10, 6, 0.5)
            .setDescription(EncryptedString.of("Max distance to search for targets"));

    private final BooleanSetting requireFall = new BooleanSetting(
            EncryptedString.of("Require Fall"), true)
            .setDescription(EncryptedString.of("Only attack while falling downward"));

    private final NumberSetting minFallHeight = new NumberSetting(
            EncryptedString.of("Min Fall Height"), 0, 20, 3, 0.5)
            .setDescription(EncryptedString.of("Minimum blocks fallen before attacking (fall damage bonus)"));

    // ── Swap ───────────────────────────────────────────────────────────────
    private final BooleanSetting autoSwap = new BooleanSetting(
            EncryptedString.of("Auto Swap"), true)
            .setDescription(EncryptedString.of("Automatically switch to mace when about to attack"));

    private final BooleanSetting swapBack = new BooleanSetting(
            EncryptedString.of("Swap Back"), true)
            .setDescription(EncryptedString.of("Switch back to previous slot after attacking"));

    private final NumberSetting swapBackDelay = new NumberSetting(
            EncryptedString.of("Swap Back Delay"), 0, 10, 2, 1)
            .setDescription(EncryptedString.of("Ticks to wait before swapping back (prevents spam)"));

    // ── Attack ─────────────────────────────────────────────────────────────
    private final NumberSetting attackDelay = new NumberSetting(
            EncryptedString.of("Attack Delay"), 0, 20, 1, 1)
            .setDescription(EncryptedString.of("Ticks between attacks to prevent spam"));

    private final NumberSetting attackChance = new NumberSetting(
            EncryptedString.of("Attack Chance"), 1, 100, 100, 1)
            .setDescription(EncryptedString.of("Randomization: percentage chance to attack each eligible tick"));

    // ── Rotation ───────────────────────────────────────────────────────────
    private final BooleanSetting autoRotate = new BooleanSetting(
            EncryptedString.of("Auto Rotate"), true)
            .setDescription(EncryptedString.of("Rotate toward nearest target"));

    private final NumberSetting rotateSpeed = new NumberSetting(
            EncryptedString.of("Rotate Speed"), 0.05, 1.0, 0.4, 0.05)
            .setDescription(EncryptedString.of("How quickly to lerp toward target (1.0 = instant)"));

    // ── Protection ─────────────────────────────────────────────────────────
    private final BooleanSetting friendProtection = new BooleanSetting(
            EncryptedString.of("Friend Protection"), true)
            .setDescription(EncryptedString.of("Never target friended players"));

    private final BooleanSetting autoShieldBreak = new BooleanSetting(
            EncryptedString.of("Auto Shield Break"), false)
            .setDescription(EncryptedString.of("Hit target with axe first to disable their shield, then swap to mace"));

    private final NumberSetting shieldBreakSlot = new NumberSetting(
            EncryptedString.of("Axe Slot"), 1, 9, 2, 1)
            .setDescription(EncryptedString.of("Hotbar slot (1-9) containing the axe for shield breaking"));

    // ── Internal state ─────────────────────────────────────────────────────
    private int previousSlot = -1;
    private int attackClock = 0;
    private int swapBackClock = 0;
    private boolean justAttacked = false;
    private boolean shieldBroken = false;
    private double fallStartY = Double.MIN_VALUE;

    public AutoMace() {
        super(EncryptedString.of("Auto Mace"),
                EncryptedString.of("Automatically attacks with the mace while falling for max fall damage"),
                -1,
                Category.COMBAT);

        addSettings(
                targetRange, requireFall, minFallHeight,
                autoSwap, swapBack, swapBackDelay,
                attackDelay, attackChance,
                autoRotate, rotateSpeed,
                friendProtection, autoShieldBreak, shieldBreakSlot
        );
    }

    @Override
    public void onEnable() {
        eventManager.add(TickListener.class, this);
        eventManager.add(HudListener.class, this);
        resetState();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        eventManager.remove(TickListener.class, this);
        eventManager.remove(HudListener.class, this);

        // Restore previous slot if we got disabled mid-sequence
        if (justAttacked && swapBack.getValue() && previousSlot >= 0) {
            mc.player.getInventory().setSelectedSlot(previousSlot);
        }

        resetState();
        super.onDisable();
    }

    // ── Called every game tick ─────────────────────────────────────────────
    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null || mc.currentScreen != null)
            return;

        // Track swap-back cooldown
        if (justAttacked) {
            swapBackClock++;
            if (swapBackClock >= swapBackDelay.getValueInt()) {
                if (swapBack.getValue() && previousSlot >= 0) {
                    mc.player.getInventory().setSelectedSlot(previousSlot);
                }
                justAttacked = false;
                swapBackClock = 0;
                shieldBroken = false;
            }
            // Don't attack again while waiting to swap back
            return;
        }

        // Tick attack delay
        if (attackClock > 0) {
            attackClock--;
            return;
        }

        // ── Find target ────────────────────────────────────────────────────
        PlayerEntity target = findTarget();
        if (target == null) {
            fallStartY = Double.MIN_VALUE;
            return;
        }

        // ── Rotation ───────────────────────────────────────────────────────
        if (autoRotate.getValue()) {
            rotateToTarget(target);
        }

        // ── Fall gate ──────────────────────────────────────────────────────
        if (requireFall.getValue()) {
            boolean isFalling = mc.player.getVelocity().y < -0.1 && !mc.player.isOnGround();

            if (!isFalling) {
                fallStartY = Double.MIN_VALUE;
                return;
            }

            // Track where the fall started
            if (fallStartY == Double.MIN_VALUE) {
                fallStartY = mc.player.getY();
            }

            double fallen = fallStartY - mc.player.getY();
            if (fallen < minFallHeight.getValue()) {
                return;
            }
        }

        // ── Chance roll ────────────────────────────────────────────────────
        if (MathUtils.randomInt(1, 100) > attackChance.getValueInt()) {
            return;
        }

        // ── Shield break ───────────────────────────────────────────────────
        if (autoShieldBreak.getValue() && !shieldBroken) {
            int axeSlot = shieldBreakSlot.getValueInt() - 1;
            int currentSlot = mc.player.getInventory().getSelectedSlot();

            if (currentSlot != axeSlot) {
                previousSlot = currentSlot;
                mc.player.getInventory().setSelectedSlot(axeSlot);
            }

            mc.interactionManager.attackEntity(mc.player, target);
            mc.player.swingHand(Hand.MAIN_HAND);
            shieldBroken = true;
            attackClock = attackDelay.getValueInt();
            return;
        }

        // ── Swap to mace ───────────────────────────────────────────────────
        if (autoSwap.getValue()) {
            int maceSlot = findMaceSlot();
            if (maceSlot < 0) return; // no mace found in hotbar

            int currentSlot = mc.player.getInventory().getSelectedSlot();
            boolean alreadyHoldingMace = mc.player.getMainHandStack().isOf(Items.MACE);

            if (!alreadyHoldingMace) {
                if (!justAttacked) {
                    previousSlot = shieldBroken ? previousSlot : currentSlot;
                }
                mc.player.getInventory().setSelectedSlot(maceSlot);
            }
        } else {
            // Manual mode — only attack if already holding mace
            if (!mc.player.getMainHandStack().isOf(Items.MACE)) return;
        }

        // ── Attack ─────────────────────────────────────────────────────────
        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);

        justAttacked = true;
        swapBackClock = 0;
        fallStartY = Double.MIN_VALUE;
        attackClock = attackDelay.getValueInt();
    }

    // Called every render frame — handles smooth rotation
    @Override
    public void onRenderHud(HudEvent event) {
        // Rotation is done in onTick for simplicity; nothing extra here.
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private PlayerEntity findTarget() {
        float range = targetRange.getValueFloat();
        PlayerEntity nearest = null;
        float minDist = Float.MAX_VALUE;

        FriendManager friendManager = Venum.INSTANCE.getFriendManager();

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;
            if (player.isDead() || player.getHealth() <= 0) continue;

            // Friend protection
            if (friendProtection.getValue() && friendManager.isFriend(player)) continue;

            float dist = (float) mc.player.distanceTo(player);
            if (dist > range) continue;

            if (dist < minDist) {
                minDist = dist;
                nearest = player;
            }
        }

        return nearest;
    }

    private void rotateToTarget(PlayerEntity target) {
        Vec3d targetPos = target.getPos().add(0, target.getHeight() / 2.0, 0);
        Rotation rotation = RotationUtils.getDirection(mc.player, targetPos);

        float speed = (float) rotateSpeed.getValue();
        float newYaw = (float) MathHelper.lerpAngleDegrees(speed, mc.player.getYaw(), (float) rotation.yaw());
        float newPitch = (float) MathHelper.lerpAngleDegrees(speed, mc.player.getPitch(), (float) rotation.pitch());

        mc.player.setYaw(newYaw);
        mc.player.setPitch(newPitch);
    }

    /** Returns hotbar slot index (0-8) of the mace, or -1 if not found. */
    private int findMaceSlot() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).isOf(Items.MACE)) {
                return i;
            }
        }
        return -1;
    }

    private void resetState() {
        previousSlot = -1;
        attackClock = 0;
        swapBackClock = 0;
        justAttacked = false;
        shieldBroken = false;
        fallStartY = Double.MIN_VALUE;
    }
}
