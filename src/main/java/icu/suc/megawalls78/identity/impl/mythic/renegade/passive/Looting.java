package icu.suc.megawalls78.identity.impl.mythic.renegade.passive;

import icu.suc.megawalls78.event.IncreaseStatsEvent;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.trait.passive.ChargePassive;
import icu.suc.megawalls78.util.InventoryUtil;
import icu.suc.megawalls78.util.ItemBuilder;
import icu.suc.megawalls78.util.PlayerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Looting extends ChargePassive {

    private static final double HEAL = 5.0D;

    private static final ItemBuilder POTION = ItemBuilder.of(Material.SPLASH_POTION)
            .setDisplayName(Component.translatable("item.minecraft.splash_potion"))
            .setAmount(2)
            .addPrefix(Identity.RENEGADE.getName().append(Component.space()))
            .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            .setMaxStackSize(64)
            .addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 240, 1), false)
            .addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 120, 0), false);

    public Looting() {
        super("looting", 5);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerKill(PlayerDeathEvent event) {
        if (PASSIVE(PlayerUtil.getKiller(event)) && CHARGE()) {
            handle(event, PLAYER());
            CHARGE_RESET();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerKill(IncreaseStatsEvent.Kill event) {
        GamePlayer player = event.getPlayer();
        if (PASSIVE(player)) {
            heal(player);
        }
    }

    private static void handle(PlayerDeathEvent event, GamePlayer gamePlayer) {
        InventoryUtil.addItem(gamePlayer.getBukkitPlayer(), event, POTION.build());
    }

    private static void heal(GamePlayer gamePlayer) {
        gamePlayer.getBukkitPlayer().heal(HEAL);
    }
}
