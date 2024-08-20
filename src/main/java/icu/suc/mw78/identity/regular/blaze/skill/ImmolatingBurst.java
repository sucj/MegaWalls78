package icu.suc.mw78.identity.regular.blaze.skill;

import icu.suc.megawalls78.identity.trait.skill.Skill;
import icu.suc.megawalls78.identity.trait.skill.task.DurationTask;
import icu.suc.megawalls78.util.EntityUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class ImmolatingBurst extends Skill {

    private static final float DAMAGE = 3F;
    private static final int DELAY = 10;
    private static final int AMOUNT = 3;
    private static final int DURATION = DELAY * AMOUNT;

    private Task task;

    public ImmolatingBurst() {
        super("immolating_burst", 100, 1000L);
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

    private static final class Task extends DurationTask {

        public Task(Player player) {
            super(player, DURATION);
        }

        @Override
        public void run() {
            if (shouldCancel()) {
                cancel();
                return;
            }

            super.run();

            if (tick % DELAY == 0) {
                Location location = player.getEyeLocation();
                EntityUtil.spawn(location, EntityUtil.Type.IMMOLATING_BURST_FIREBALL, null, player, location.getDirection(), DAMAGE);
            }
        }
    }
}
