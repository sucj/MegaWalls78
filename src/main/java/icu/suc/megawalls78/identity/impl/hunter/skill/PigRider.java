package icu.suc.megawalls78.identity.impl.hunter.skill;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.identity.trait.skill.DurationSkill;
import icu.suc.megawalls78.identity.trait.skill.task.DurationTask;
import icu.suc.megawalls78.util.EntityUtil;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;

public final class PigRider extends DurationSkill {

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
            task.runTaskTimer(MegaWalls78.getInstance(), 0L, 1L);
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
                pig.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.5D);
                pig.setInvulnerable(true);
                pig.addPassenger(player);
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
