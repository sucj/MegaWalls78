package icu.suc.megawalls78.identity.impl.assassin.passive;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.Passive;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;

public final class MasterAlchemist extends Passive implements IActionbar {
    //<开始时的时间戳，累计受到的伤害>
    private static final long COOLDOWN = 12 * 20;
    boolean inCD = false;
    HashMap<Long, Double> dmgHandleMap = new HashMap<>();

    public MasterAlchemist() {
        super("master_alchemist");
    }

    @EventHandler
    public void onDMGTaken(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if ((event.getDamageSource().getCausingEntity() instanceof Player dmger) && (event.getEntity() instanceof Player player)) {
            if (shouldPassive(player) && !inCD) {
                double dmg = event.getDamage();
                //对于还未超时的伤害跟踪，加之。
                //对于已超时者，除之。这代表着在受击1s时间内并未受到大于等于10hp伤害
                //对于正好到时间(取等)你管他干嘛。
                for (long timeMillis : dmgHandleMap.keySet()) {
                    if (System.currentTimeMillis() - timeMillis < 1000) {
                        dmgHandleMap.replace(timeMillis, dmgHandleMap.get(timeMillis) + dmg);
                    } else if (System.currentTimeMillis() - timeMillis > 1000) {
                        dmgHandleMap.remove(timeMillis);
                    }
                }


                //开始新的监听
                long timeMillis = System.currentTimeMillis();
                dmgHandleMap.put(timeMillis, 0.0);
                Bukkit.getScheduler().runTaskLater(MegaWalls78.getInstance(), () -> {
                    if (dmgHandleMap.get(timeMillis) != null && (dmgHandleMap.get(timeMillis) >= 10)) {
                        inCD = true;
                        Bukkit.getScheduler().runTaskLater(MegaWalls78.getInstance(), () -> {
                            inCD = false;
                        }, COOLDOWN);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 2));
                    }
                }, 20);
            }
        }
    }


    @Override
    public void unregister() {

    }

    @Override
    public Component acb() {
        return Type.MODE.accept(Component.text(inCD ? "Not Ready" : "Ready"));
    }

    public Location getBlockBehindPlayer(Player player) {
        Vector inverseDirectionVec = player.getLocation().getDirection().normalize().multiply(-1);
        return player.getLocation().add(inverseDirectionVec);
    }
}
