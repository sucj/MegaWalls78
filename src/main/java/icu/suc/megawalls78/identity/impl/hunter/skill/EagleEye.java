package icu.suc.megawalls78.identity.impl.hunter.skill;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.entity.HomingArrow;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.identity.trait.skill.DurationSkill;
import icu.suc.megawalls78.identity.trait.skill.task.DurationTask;
import icu.suc.megawalls78.util.EntityUtil;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public final class EagleEye extends DurationSkill {

    private static final double MELEE = 0.25D;
    private static final double ARROW = 1.0D;

    public EagleEye() {
        super("eagle_eye", 100, 1000L, 10000L, Internal.class);
    }

    private Task task;

    @Override
    protected boolean use0(Player player) {

        boolean run = false;
        if (task == null || task.isCancelled()) {
            task = new Task(player);
            run = true;
        }

        task.resetTimer();

        if (run) {
            task.runTaskTimer(MegaWalls78.getInstance(), 0L, 1L);
        }

        return true;
    }

    private final class Task extends DurationTask {

        private Task(Player player) {
            super(player, (int) (EagleEye.this.getDuration() / 50));

            EntityUtil.setMetadata(player, EagleEye.this.getId(), true);
        }

        @Override
        public void run() {
            if (shouldCancel()) {
                cancel();
                return;
            }

            super.run();
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            EntityUtil.removeMetadata(player, EagleEye.this.getId());
            super.cancel();
            stop();
        }
    }

    public static final class Internal extends Passive {

        public Internal() {
            super("eagle_eye");
        }

        @EventHandler(ignoreCancelled = true)
        public void onPlayerShoot(ProjectileLaunchEvent event) {
            if (event.getEntity() instanceof Arrow arrow && PASSIVE(arrow.getOwnerUniqueId()) && EntityUtil.getMetadata(PLAYER().getBukkitPlayer(), getId()) && condition_arrow(arrow)) {
                arrow.remove();
                EntityUtil.spawn(arrow.getLocation(), EntityUtil.Type.HOMING_ARROW, entity -> {
                    Arrow homingArrow = (Arrow) entity;
                    EntityUtil.setMetadata(homingArrow, HomingArrow.HOMING, true);
                    homingArrow.setVelocity(arrow.getVelocity());
                    homingArrow.setCritical(true);
                }, PLAYER().getBukkitPlayer(), arrow.getItemStack(), arrow.getWeapon(), 4.0F);
            }
        }

        @EventHandler(ignoreCancelled = true)
        public void onPlayerAttack(EntityDamageByEntityEvent event) {
            if (event.getDamager() instanceof Player player && PASSIVE(player) && EntityUtil.getMetadata(player, getId()) && condition_attack(event)) {
                if (condition_melee(event)) {
                    player.heal(MELEE);
                } else if (condition_arrow(event)) {
                    player.heal(ARROW);
                }
            }
        }

        private static boolean condition_arrow(Arrow arrow) {
            return arrow.isCritical() && !EntityUtil.getMetadata(arrow, HomingArrow.HOMING);
        }

        private static boolean condition_attack(EntityDamageByEntityEvent event) {
            return event.getEntity() instanceof Player;
        }

        private static boolean condition_melee(EntityDamageByEntityEvent event) {
            return EntityUtil.isMeleeAttack(event);
        }

        private static boolean condition_arrow(EntityDamageByEntityEvent event) {
            return EntityUtil.isArrowAttack(event) && EntityUtil.getMetadata(event.getDamager(), HomingArrow.HOMING);
        }
    }
}
