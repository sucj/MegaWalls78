package icu.suc.mw78.identity.regular.hunter.skill;

import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.skill.DurationSkill;
import icu.suc.megawalls78.identity.trait.skill.task.DurationTask;
import icu.suc.megawalls78.util.EntityUtil;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Trait(value = "pig_rider", cost = 0F, cooldown = 60000L, duration = 30000L)
public final class PigRider extends DurationSkill {

    private static final PotionEffect SPEED = new PotionEffect(PotionEffectType.SPEED, 10, 4, true, false);

    private Task task;

    @Override
    protected boolean use0(Player player) {
        boolean run = false;
        if (task == null || task.isCancelled()) {
            task = new Task(player);
            run = true;
        }

        task.reset();

        if (run) {
            task.fire();
        }
        return true;
    }

    private final class Task extends DurationTask {

        private Pig pig;

        public Task(Player player) {
            super(player, (int) (DURATION_GET() / 50));

            spawn();
        }

        @Override
        public void run() {
            if (shouldCancel()) {
                cancel();
                return;
            }

            if (pig.getPassengers().isEmpty()) {
                cancel();
                return;
            }

            super.run();
        }

        private void spawn() {
            EntityUtil.spawn(player.getLocation(), EntityUtil.Type.CONTROLLABLE_PIG, entity -> {
                pig = (Pig) entity;
                EntityUtil.scaleAttributeBaseValue(pig, Attribute.GENERIC_MOVEMENT_SPEED, 2.5D);
                pig.setInvulnerable(true);
                pig.addPotionEffect(SPEED);
                EntityUtil.safeRide(pig, player);
            });
        }

        private void discard() {
            pig.remove();
            pig = null;
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            discard();
            super.cancel();
            stop();
        }
    }
}
