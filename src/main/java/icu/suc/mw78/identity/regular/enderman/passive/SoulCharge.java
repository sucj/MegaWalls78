package icu.suc.mw78.identity.regular.enderman.passive;

import icu.suc.megawalls78.event.EnergyChangeEvent;
import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.passive.CooldownPassive;
import icu.suc.megawalls78.util.Effect;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Trait("soul_charge")
public final class SoulCharge extends CooldownPassive {

    private static final int ENERGY = 100;

    private static final PotionEffect REGENERATION = new PotionEffect(PotionEffectType.REGENERATION, 200, 0);

    private static final Effect<Player> EFFECT_SKILL = Effect.create(player -> player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_ENDERMAN_SCREAM, SoundCategory.PLAYERS, 1.0F, 1.0F));

    public SoulCharge() {
        super(15000L);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEnergyChange(EnergyChangeEvent event) {
        Player player = event.getPlayer();
        if (PASSIVE(player) && COOLDOWN() && condition(event)) {
            EFFECT_SKILL.play(player);
            potion(player);
            COOLDOWN_RESET();
        }
    }

    private static boolean condition(EnergyChangeEvent event) {
        return event.getEnergy() == ENERGY;
    }

    private void potion(Player player) {
        player.addPotionEffect(REGENERATION);
        summaryEffectSelf(player, REGENERATION);
    }
}