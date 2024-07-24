package icu.suc.megawalls78.identity.impl.assassin.passive;

import com.google.common.collect.Maps;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.Passive;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;

public final class MasterAlchemist extends Passive implements IActionbar {

    private static final long COOLDOWN = 12000L;
    private static final long TIME = 1000L;
    private static final double DAMAGE = 10.0D;
    private static final PotionEffect REGENERATION = new PotionEffect(PotionEffectType.REGENERATION, 100, 2);

    private final MutablePair<Long, Double> lastDamage = MutablePair.of(0L, 0.0D);
    private final HashMap<Long, Double> dmgHandleMap = Maps.newHashMap();

    private long lastMills;

    public MasterAlchemist() {
        super("master_alchemist");
    }

    @EventHandler
    public void onDMGTaken(EntityDamageEvent event) { // 实际上这个技能不需要在意是谁造成了伤害
        if (event.isCancelled()) {
            return;
        }

        if (event.getEntity() instanceof Player player && shouldPassive(player)) {
            long currentMillis = System.currentTimeMillis();
            if (currentMillis - lastMills >= COOLDOWN) {
                lastMills = currentMillis;

                double damage = event.getFinalDamage();
                long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis - lastDamage.getLeft() <= TIME) {
                    lastDamage.setRight(lastDamage.getRight() + damage);
                } else {
                    lastDamage.setLeft(currentTimeMillis);
                    lastDamage.setRight(damage);
                }

                if (lastDamage.getRight() > DAMAGE) {
                    player.addPotionEffect(REGENERATION);
                    lastDamage.setLeft(0L);
                }
            }
        }
    }


    @Override
    public void unregister() {

    }

    @Override
    public Component acb() {
        return Type.COOLDOWN.accept(System.currentTimeMillis(), lastMills, COOLDOWN);
    }
}
