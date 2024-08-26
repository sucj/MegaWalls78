package icu.suc.megawalls78.identity.trait.skill.task;

import org.bukkit.entity.Player;

public abstract class DurationTask extends AbstractTask {

    protected final int max;
    protected int tick;

    public DurationTask(Player player, int max) {
        super(player);
        this.max = max - 1;
        reset();
    }

    @Override
    public void run() {
        tick++;
    }

    @Override
    protected boolean shouldCancel() {
        return super.shouldCancel() || tick >= max;
    }

    public void reset() {
        this.tick = -1;
    }

    public int remain() {
        return max - tick;
    }
}
