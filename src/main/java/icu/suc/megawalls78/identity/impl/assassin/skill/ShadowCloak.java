package icu.suc.megawalls78.identity.impl.assassin.skill;

import com.google.common.collect.Maps;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.identity.trait.Skill;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

// 因为即将重构Passive的计时方式顺便加入Skill的监听实现，暂时推迟隐身时攻击的编写
public class ShadowCloak extends Skill {

    private static final long DURATION = 10000L; // 持续10秒
    private static final int TICK = (int) (DURATION / 50);
    private static final PotionEffect INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY, TICK, 0); // 关于隐藏盔甲，将以后以Util形式提供发级别的隐藏
    private static final PotionEffect SPEED = new PotionEffect(PotionEffectType.SPEED, TICK, 0);
    private static final PotionEffect RESISTANCE = new PotionEffect(PotionEffectType.RESISTANCE, TICK, 0);
    private static final int REMAIN = 200; // 每剩余1秒隐身时间
    private static final int RETURN = 4; // 返还4点能量
    private static final double SCALE = 0.1D; //损失的生命值10%
    private static final double MIN = 1.0D; // 至少造成1点真实伤害

    private static final Map<UUID, Boolean> PLAYER_STATE = Maps.newHashMap();

    private Task task;

    public ShadowCloak() {
        super("shadow_cloak", 100, 1000L);
    }

    @Override
    protected boolean use0(Player player) {
        // 关于无线续杯问题，蜘蛛提供了一个很好的模板，请自行查阅
        boolean run = false;
        if (task == null || task.isCancelled()) {
            task = new Task(player);
            run = true;
        }

        player.addPotionEffect(INVISIBILITY); // force参数已弃用，默认覆盖原效果时间
        player.addPotionEffect(SPEED);
        player.addPotionEffect(RESISTANCE);
        task.resetTimer();

        if (run) {
            task.runTaskTimer(MegaWalls78.getInstance(), 0L, 1L);
        }

        //TODO:不是asn隐身的粒子效果是什么来着老子忘了草拟吗反正肯定不是dust但我也不知道是什么所以等会再补
        return true;
    }

    // 按照技能开发条例第六章第四条：主动技能如有持续效果必须使用Runnable
    private final class Task extends BukkitRunnable {

        private final Player player;

        private int tick; // 持续时间

        private Task(Player player) {
            this.player = player;

            setState(player.getUniqueId(), true);
        }

        @Override
        public void run() {
            if (player.isDead()) {
                this.cancel();
                return;
            }

            if (tick >= TICK) {
                this.cancel();
                return;
            }

            tick++;
        }

        public void resetTimer() {
            this.tick = 0;
        }

        // 主动破隐时调用
        public void appear() {
            int remain = tick - TICK;
            if (remain > 0) {
                MegaWalls78.getInstance().getGameManager().getPlayer(player).increaseEnergy(remain / REMAIN * RETURN);
                this.cancel();
            }
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            setState(player.getUniqueId(), false);
            super.cancel();
        }
    }

    public static boolean getState(UUID uuid) {
        return PLAYER_STATE.computeIfAbsent(uuid, t -> false);
    }

    public static void setState(UUID uuid, boolean state) {
        PLAYER_STATE.put(uuid, state);
    }
}
