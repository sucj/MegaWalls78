package icu.suc.megawalls78.identity.impl.squid.passive;

import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.util.Effect;
import icu.suc.megawalls78.util.EntityUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.atomic.AtomicInteger;

import static icu.suc.megawalls78.util.PlayerUtil.isValidAllies;

public class InnerInk extends Passive {

    private static final double RADIUS = 5.0D;

    private static final PotionEffect BLINDNESS = new PotionEffect(PotionEffectType.BLINDNESS, 60, 0);

    private static final Effect<Player> EFFECT_SOUND = Effect.create(player -> player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F));

    public InnerInk() {
        super("inner_ink");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPotionConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        AtomicInteger count = new AtomicInteger();
        if (PASSIVE(player) && condition(event)) {

            EntityUtil.getNearbyEntities(player, RADIUS).stream()
                    .filter(entity -> entity instanceof Player)
                    .filter(entity -> !isValidAllies(player, entity))
                    .forEach(entity -> {
                        potion((Player) entity, player);
                        count.getAndIncrement();
                    });

            EFFECT_SOUND.play(player);
            summaryHit(player, count.get());
        }
    }

    private static boolean condition(PlayerItemConsumeEvent event) {
        return event.getItem().getType().equals(Material.POTION);
    }

    private void potion(Player target, Player source) {
        EntityUtil.addPotionEffect(target, BLINDNESS, source);
        summaryEffectOther(source, target, BLINDNESS);
    }
}
