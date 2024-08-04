package icu.suc.megawalls78.identity.trait;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.management.GameManager;
import icu.suc.megawalls78.util.ComponentUtil;
import icu.suc.megawalls78.util.Formatters;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

public abstract class Trait {

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

    protected boolean summaryHeal(Player player, int count) {
        ComponentUtil.sendMessage(Component.translatable("mw78.summary.heal", NamedTextColor.AQUA, name(player), Component.text(Formatters.NUMBER.format(count), NamedTextColor.GREEN)), player);
        return true;
    }

    protected boolean summaryHit(Player player, int count) {
        ComponentUtil.sendMessage(Component.translatable("mw78.summary.hit", NamedTextColor.AQUA, name(player), Component.text(Formatters.NUMBER.format(count), NamedTextColor.RED)), player);
        return true;
    }

    protected void summaryHealBy(Player player, Player target) {
        ComponentUtil.sendMessage(Component.translatable("mw78.summary.heal_by", NamedTextColor.GREEN, name(player), player.teamDisplayName()), target);
    }

    protected void summaryArrows(Player player, Player target, int count) {
        ComponentUtil.sendMessage(Component.translatable("mw78.summary.arrows", NamedTextColor.AQUA, target.teamDisplayName(), Component.text(Formatters.NUMBER.format(count), NamedTextColor.YELLOW)), player);
    }

    protected void summaryUnique(Player player, int count) {
        ComponentUtil.sendMessage(Component.translatable("mw78.summary.unique", NamedTextColor.AQUA, Component.text(Formatters.NUMBER.format(count), NamedTextColor.RED)), player);
    }

    protected void refund(Player player, int count) {
        PLAYER().increaseEnergy(count);
        ComponentUtil.sendMessage(Component.translatable("mw78.summary.energy", NamedTextColor.AQUA, name(player), Component.text(count, NamedTextColor.YELLOW)), player);
    }

    protected Component name(Player player) {
        return getName().color(MegaWalls78.getInstance().getGameManager().getPlayer(player).getIdentity().getColor()).decorate(TextDecoration.BOLD);
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
