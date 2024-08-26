package icu.suc.mw78.identity.regular.blaze.skill;

import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.skill.Skill;
import icu.suc.megawalls78.identity.trait.skill.task.DurationTask;
import icu.suc.megawalls78.util.Effect;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.ParticleUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;

@Trait(value = "immolating_burst", cost = 100F, cooldown = 1000L)
public final class ImmolatingBurst extends Skill {

    private static final float DAMAGE = 3F;
    private static final int DELAY = 10;
    private static final int AMOUNT = 3;
    private static final int DURATION = DELAY * AMOUNT;

    private static final Effect<Player> EFFECT_SKILL = Effect.create(player -> {
        ParticleUtil.spawnParticleRandomBody(player, Particle.LAVA, 4);
        player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);
    });

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
                EFFECT_SKILL.play(player);
                Location location = player.getEyeLocation();
                EntityUtil.spawn(location, EntityUtil.Type.IMMOLATING_BURST_FIREBALL, null, player, location.getDirection(), DAMAGE);
            }
        }
    }
}
