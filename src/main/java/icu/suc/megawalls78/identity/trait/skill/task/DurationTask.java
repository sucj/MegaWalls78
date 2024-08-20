package icu.suc.megawalls78.identity.trait.skill.task;

import org.bukkit.entity.Player;

public abstract class DurationTask extends AbstractTask {

    protected final int maxTick;
    protected int tick;

    public DurationTask(Player player, int maxTick) {
        super(player);
        this.maxTick = maxTick - 1;
        resetTimer();
    }

    @Override
    public void run() {
        tick++;
    }

    @Override
    protected boolean shouldCancel() {
        return super.shouldCancel() || tick >= maxTick;
    }

    public void resetTimer() {
        this.tick = -1;
    }

    public int remain() {
        return maxTick - tick;
    }
}
