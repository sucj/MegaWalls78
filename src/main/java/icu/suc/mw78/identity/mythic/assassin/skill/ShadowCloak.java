package icu.suc.mw78.identity.mythic.assassin.skill;

import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.identity.trait.skill.DurationSkill;
import icu.suc.megawalls78.identity.trait.skill.task.DurationTask;
import icu.suc.megawalls78.util.Effect;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Trait("shadow_cloak")
public final class ShadowCloak extends DurationSkill {

    public static final String ID = "shadow_cloak";

    private static final long DURATION = 10000L; // 持续10秒
    private static final int TICK = (int) (DURATION / 50);
    private static final PotionEffect INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY, TICK, 0); // 关于隐藏盔甲，将以后以Util形式提供发级别的隐藏
    private static final PotionEffect SPEED = new PotionEffect(PotionEffectType.SPEED, TICK, 0);
    private static final PotionEffect RESISTANCE = new PotionEffect(PotionEffectType.RESISTANCE, TICK, 0);
    private static final int REMAIN = 20; // 每剩余1秒隐身时间
    private static final int RETURN = 4; // 返还4点能量
    private static final double SCALE = 0.1D; //损失的生命值10%
    private static final double MIN = 1.0D; // 至少造成1点真实伤害

    private static final Effect<Player> EFFECT_START = Effect.create(player -> {
        ParticleUtil.spawnParticleRandomBody(player, Particle.SMOKE, 8, 0);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_AMBIENT, SoundCategory.PLAYERS, 1.0F, 2.0F);
    });
    private static final Effect<Player> EFFECT_END = Effect.create(player -> player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 1.0F, 1.0F));

    private Task task;

    public ShadowCloak() {
        super(100, 1000L, DURATION, Internal.class);
    }

    @Override
    protected boolean use0(Player player) {
        // 关于无线续杯问题，蜘蛛提供了一个很好的模板，请自行查阅
        boolean run = false;
        if (task == null || task.isCancelled()) {
            task = new Task(player);
            run = true;
        }

        EFFECT_START.play(player);
        player.addPotionEffect(INVISIBILITY); // force参数已弃用，默认覆盖原效果时间
        player.addPotionEffect(SPEED);
        player.addPotionEffect(RESISTANCE);
        summaryEffectSelf(player, INVISIBILITY, SPEED, RESISTANCE);
        task.reset();

        if (run) {
            task.fire();
        }

        return true;
    }

    private final class Task extends DurationTask {

        private Task(Player player) {
            super(player, TICK);

            EntityUtil.setMetadata(player, getId(), true);
//            updateArmor();
        }

        @Override
        public void run() {
            if (shouldCancel()) {
                cancel();
                return;
            }

            super.run();

            if (!EntityUtil.getMetadata(player, getId())) {
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                if (EntityUtil.hasPotionEffect(player, RESISTANCE)) {
                    player.removePotionEffect(PotionEffectType.RESISTANCE);
                }
                ShadowCloak.this.summaryRefund(player, (int) (((double) remain() / REMAIN) * RETURN));
                cancel();
            }
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            EFFECT_END.play(player);
            EntityUtil.removeMetadata(player, ShadowCloak.this.getId());
            super.cancel();
            stop();
        }
    }

    public static final class Internal extends Passive {

        @EventHandler(ignoreCancelled = true)
        public void onPlayerDamage(EntityDamageByEntityEvent event) {
            if (event.getDamager() instanceof Player player && PASSIVE(player) && EntityUtil.getMetadata(player, getId()) && condition(event)) {
                LivingEntity entity = (LivingEntity) event.getEntity();
                double damage = Math.max((entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() - entity.getHealth()) * SCALE, MIN);
                event.setDamage(event.getDamage() + damage);
                EntityUtil.removeMetadata(player, getId());
            }
        }

        private static boolean condition(EntityDamageByEntityEvent event) {
            Entity entity = event.getEntity();
            return entity instanceof Player || entity instanceof Wither;
        }
    }
}
