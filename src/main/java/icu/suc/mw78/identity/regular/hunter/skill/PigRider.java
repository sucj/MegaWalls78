package icu.suc.mw78.identity.regular.hunter.skill;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.identity.trait.skill.DurationSkill;
import icu.suc.megawalls78.identity.trait.skill.task.DurationTask;
import icu.suc.megawalls78.util.EntityUtil;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class PigRider extends DurationSkill {

    private static final PotionEffect SPEED = new PotionEffect(PotionEffectType.SPEED, 10, 4, true, false);

    private Task task;

    public PigRider() {
        super("pig_rider", 0, 60000L, 30000L);
    }

    @Override
    protected boolean use0(Player player) {
        boolean run = false;
        if (task == null || task.isCancelled()) {
            task = new Task(player);
            run = true;
        }

        task.resetTimer();

        if (run) {
            task.fire();
        }
        return true;
    }

    private final class Task extends DurationTask {

        private Pig pig;

        public Task(Player player) {
            super(player, (int) (getDuration() / 50));

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
                AttributeInstance attribute = pig.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                attribute.setBaseValue(attribute.getBaseValue() * 2.5);
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
