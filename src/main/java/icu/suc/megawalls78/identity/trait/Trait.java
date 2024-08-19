package icu.suc.megawalls78.identity.trait;

import com.google.common.collect.Lists;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.util.ComponentUtil;
import icu.suc.megawalls78.util.Formatters;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.List;

public abstract class Trait {

    private static final JoinConfiguration EFFECT_JOIN = JoinConfiguration.builder().separator(Component.translatable("mw78.summary.effect.join")).build();

    private final String id;
    private final Component name;

    private GamePlayer player;

    public Trait(String id, Component name) {
        this.id = id;
        this.name = name;
    }

    protected boolean noTarget(Player player) {
        ComponentUtil.sendMessage(Component.translatable("mw78.summary.no_target", NamedTextColor.RED, name(player)), player);
        return false;
    }

    protected boolean summaryHealSelf(Player player) {
        ComponentUtil.sendMessage(Component.translatable("mw78.summary.heal.self", NamedTextColor.AQUA, name(player)), player);
        return true;
    }

    protected void summaryHealOther(Player player, Player target) {
        ComponentUtil.sendMessage(Component.translatable("mw78.summary.heal_by", NamedTextColor.GREEN, name(player), player.teamDisplayName()), target);
    }

    protected boolean summaryHit(Player player, int count) {
        ComponentUtil.sendMessage(Component.translatable("mw78.summary.hit", NamedTextColor.AQUA, name(player), Component.text(Formatters.NUMBER.format(count), NamedTextColor.RED)), player);
        return true;
    }

    protected void summaryArrows(Player player, Player target, int count) {
        ComponentUtil.sendMessage(Component.translatable("mw78.summary.arrows", NamedTextColor.AQUA, target.teamDisplayName(), Component.text(Formatters.NUMBER.format(count), NamedTextColor.YELLOW)), player);
    }

    protected void summaryUnique(Player player, int count) {
        ComponentUtil.sendMessage(Component.translatable("mw78.summary.unique", NamedTextColor.AQUA, Component.text(Formatters.NUMBER.format(count), NamedTextColor.RED)), player);
    }

    protected void summaryRefund(Player player, int count) {
        PLAYER().increaseEnergy(count);
        ComponentUtil.sendMessage(Component.translatable("mw78.summary.energy", NamedTextColor.AQUA, name(player), Component.text(count, NamedTextColor.YELLOW)), player);
    }

    protected boolean summaryEffectSelf(Player player, Collection<PotionEffect> potionEffects) {
        return summaryEffectSelf(player, potionEffects.toArray(new PotionEffect[0]));
    }

    protected boolean summaryEffectSelf(Player player, PotionEffect... potionEffects) {
        if (potionEffects.length == 0) {
            return true;
        }
        ComponentUtil.sendMessage(Component.translatable("mw78.summary.effect.self", NamedTextColor.AQUA, effects(potionEffects), name(player)), player);
        return true;
    }

    protected void summaryEffectOther(Player player, Player target, PotionEffect... potionEffects) {
        ComponentUtil.sendMessage(Component.translatable("mw78.summary.effect.other", NamedTextColor.AQUA, effects(potionEffects), name(player), player.teamDisplayName()), target);
    }

    private Component name(Player player) {
        return getName().color(MegaWalls78.getInstance().getGameManager().getPlayer(player).getIdentity().getColor()).decorate(TextDecoration.BOLD);
    }

    protected Component effects(PotionEffect... potionEffects) {
        List<Component> effects = Lists.newArrayList();
        for (PotionEffect potionEffect : potionEffects) {
            PotionEffectType type = potionEffect.getType();
            effects.add(Component.translatable(type).append(Component.space()).append(Component.translatable("enchantment.level." + (potionEffect.getAmplifier() + 1))).color(TextColor.color(type.getColor().asRGB())).decorate(TextDecoration.BOLD));
        }
        return Component.join(EFFECT_JOIN, effects);
    }

    public String getId() {
        return id;
    }

    public Component getName() {
        return name;
    }


    public GamePlayer PLAYER() {
        return player;
    }

    public void PLAYER(GamePlayer player) {
        this.player = player;
    }
}
