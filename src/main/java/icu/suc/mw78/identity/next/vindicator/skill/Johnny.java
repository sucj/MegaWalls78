package icu.suc.mw78.identity.next.vindicator.skill;

import icu.suc.megawalls78.identity.trait.annotation.Trait;
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

@Trait(value = "johnny", cost = 100F, cooldown = 1000L, duration = 5000L, internal = Johnny.Internal.class)
public final class Johnny extends DurationSkill {

    private static final Set<EntityDamageEvent.DamageModifier> MODIFIERS = Set.of(EntityDamageEvent.DamageModifier.ARMOR, EntityDamageEvent.DamageModifier.RESISTANCE);

    private static final Effect<Player> EFFECT_SKILL = Effect.create(player -> player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_VINDICATOR_CELEBRATE, SoundCategory.PLAYERS, 1.0F, 1.0F));

    private Task task;

    @Override
    protected boolean use0(Player player) {

        boolean run = false;
        if (task == null || task.isCancelled()) {
            task = new Task(player);
            run = true;
        }

        EFFECT_SKILL.play(player);
        task.reset();

        if (run) {
            task.fire();
        }
        return true;
    }

    private final class Task extends DurationTask {

        public Task(Player player) {
            super(player, 100);

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
