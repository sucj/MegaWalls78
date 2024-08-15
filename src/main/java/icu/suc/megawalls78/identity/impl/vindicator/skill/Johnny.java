package icu.suc.megawalls78.identity.impl.vindicator.skill;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.identity.trait.skill.DurationSkill;
import icu.suc.megawalls78.identity.trait.skill.task.DurationTask;
import icu.suc.megawalls78.util.Effect;
import icu.suc.megawalls78.util.EntityUtil;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Set;

public final class Johnny extends DurationSkill {

    private static final long DURATION = 5000L;
    private static final int TICK = (int) (DURATION / 50);

    private static final Set<EntityDamageEvent.DamageModifier> MODIFIERS = Set.of(EntityDamageEvent.DamageModifier.ARMOR, EntityDamageEvent.DamageModifier.RESISTANCE);

    private static final Effect<Player> EFFECT_SKILL = Effect.create(player -> player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_VINDICATOR_CELEBRATE, SoundCategory.PLAYERS, 1.0F, 1.0F));

    private Task task;

    public Johnny() {
        super("johnny", 100, 1000L, DURATION, Internal.class);
    }

    @Override
    protected boolean use0(Player player) {

        boolean run = false;
        if (task == null || task.isCancelled()) {
            task = new Task(player);
            run = true;
        }

        EFFECT_SKILL.play(player);
        task.resetTimer();

        if (run) {
            task.runTaskTimer(MegaWalls78.getInstance(), 0L, 1L);
        }
        return true;
    }

    private final class Task extends DurationTask {

        public Task(Player player) {
            super(player, TICK);

            EntityUtil.setMetadata(player, getId(), true);
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
            EntityUtil.removeMetadata(player, getId());
            super.cancel();
            stop();
        }
    }

    public static final class Internal extends Passive {

        public Internal() {
            super("johnny");
        }

        @EventHandler(ignoreCancelled = true)
        public void onPlayerAttack(EntityDamageByEntityEvent event) {
            if (event.getDamager() instanceof Player player && PASSIVE(player) && EntityUtil.getMetadata(player, getId()) && condition(event)) {
                handle(event);
            }
        }

        private static boolean condition(EntityDamageByEntityEvent event) {
            return EntityUtil.isMeleeAttack(event);
        }

        private static void handle(EntityDamageByEntityEvent event) {
            for (EntityDamageEvent.DamageModifier modifier : MODIFIERS) {
                if (event.isApplicable(modifier)) {
                    event.setDamage(modifier, 0);
                }
            }
        }
    }
}
