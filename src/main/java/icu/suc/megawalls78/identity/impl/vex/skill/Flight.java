package icu.suc.megawalls78.identity.impl.vex.skill;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.identity.trait.skill.DurationSkill;
import icu.suc.megawalls78.identity.trait.skill.task.DurationTask;
import icu.suc.megawalls78.util.Effect;
import icu.suc.megawalls78.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public final class Flight extends DurationSkill {

    private static final long DURATION = 10000L;
    private static final int TICK = (int) (DURATION / 50);

    private static final Effect<Player> EFFECT_SOUND = Effect.create(player -> player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_VEX_AMBIENT, SoundCategory.PLAYERS, 1.0F, 1.0F));
    private static final Effect<Player> EFFECT_PARTICLE = Effect.create(player -> ParticleUtil.spawnParticleRandomBody(player, Particle.WHITE_SMOKE, 1, 0));

    private Task task;

    public Flight() {
        super("flight", 100, 10000L, DURATION);
    }

    @Override
    protected boolean use0(Player player) {

        boolean run = false;
        if (task == null || task.isCancelled()) {
            task = new Task(player);
            run = true;
        }

        EFFECT_SOUND.play(player);
        task.resetTimer();

        if (run) {
            task.runTaskTimer(MegaWalls78.getInstance(), 0L, 1L);
        }

        return true;
    }

    private final class Task extends DurationTask {

        public Task(Player player) {
            super(player, TICK);

            player.setAllowFlight(true);
            player.setFlying(true);
        }

        @Override
        public void run() {
            if (shouldCancel()) {
                cancel();
                return;
            }

            super.run();

            EFFECT_PARTICLE.play(player);
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            player.setAllowFlight(false);
            super.cancel();
            stop();
        }
    }
}
