package icu.suc.megawalls78.identity.trait;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

public abstract class Trait {

    private final String id;
    private final Component name;

    public Trait(String id, Component name) {
        this.id = id;
        this.name = name;
    }

    protected void summaryHeal(Player player, int count) {
        ComponentUtil.sendMessage(Component.translatable("mw78.summary.heal", NamedTextColor.AQUA, getName().color(MegaWalls78.getInstance().getGameManager().getPlayer(player).getIdentity().getColor()).decorate(TextDecoration.BOLD), Component.text(count, NamedTextColor.GREEN)), player);
    }

    protected void summaryHit(Player player, int count) {
        ComponentUtil.sendMessage(Component.translatable("mw78.summary.hit", NamedTextColor.AQUA, getName().color(MegaWalls78.getInstance().getGameManager().getPlayer(player).getIdentity().getColor()).decorate(TextDecoration.BOLD), Component.text(count, NamedTextColor.RED)), player);
    }

    public String getId() {
        return id;
    }

    public Component getName() {
        return name;
    }
}
