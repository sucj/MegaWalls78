package icu.suc.megawalls78.identity.impl.assassin.skill;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.identity.trait.Skill;
import icu.suc.megawalls78.util.ParticleUtil;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ShadowCloak extends Skill {
    static long latestShadowCloakTimeStamp = 0;
    public boolean inShadowCloak = false;

    public ShadowCloak() {
        super("shadow_cloak", 100, 0L);
    }

    @Override
    protected boolean use0(Player player) {
        if (inShadowCloak) {
            return false;
        }
        inShadowCloak = true;
        long shadowCloakTimeStamp = System.currentTimeMillis();
        latestShadowCloakTimeStamp = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskLater(MegaWalls78.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (shadowCloakTimeStamp == latestShadowCloakTimeStamp) {
                    //为了保证玩家有能够在shadow cloak上无限续杯的机会（不过挺难的就是了），如果取消程序老革命跟不上新形势了，那就剥夺它的政治权终身！
                    inShadowCloak = false;
                }
            }
        }, 20 * 10);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 10, 0), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 10, 0), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20 * 10, 0), true);
        ParticleUtil.playExpandingCircleParticle(player.getLocation(), Particle.DUST, 10, 2, 1000L);

        //TODO:不是asn隐身的粒子效果是什么来着老子忘了草拟吗反正肯定不是dust但我也不知道是什么所以等会再补
        return true;
    }

}
