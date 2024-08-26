package icu.suc.mw78.identity.next.vex.skill;

import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.skill.DurationSkill;
import icu.suc.megawalls78.identity.trait.skill.task.DurationTask;
import icu.suc.megawalls78.util.Effect;
import icu.suc.megawalls78.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

@Trait(value = "flight", cost = 100F, cooldown = 10000L, duration = 5000L)
public final class Flight extends DurationSkill {

    private static final Effect<Player> EFFECT_SOUND = Effect.create(player -> player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_VEX_AMBIENT, SoundCategory.PLAYERS, 1.0F, 1.0F));
    private static final Effect<Player> EFFECT_PARTICLE = Effect.create(player -> ParticleUtil.spawnParticleRandomBody(player, Particle.WHITE_SMOKE, 1, 0));

    private Task task;

    @Override
    protected boolean use0(Player player) {

        boolean run = false;
        if (task == null || task.isCancelled()) {
            task = new Task(player);
            run = true;
        }

        EFFECT_SOUND.play(player);
        task.reset();

        if (run) {
            task.fire();
        }

        return true;
    }

    private final class Task extends DurationTask {

        public Task(Player player) {
            super(player, 100);

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
