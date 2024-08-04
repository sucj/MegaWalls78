package icu.suc.megawalls78.identity.trait.passive;

import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.identity.trait.Trait;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.UUID;

public abstract class Passive extends Trait implements Listener {

    public Passive(String id) {
        super(id, Component.translatable("mw78.passive." + id));
    }

    protected boolean PASSIVE(Player player) {
        return player != null && PASSIVE(player.getUniqueId());
    }

    protected boolean PASSIVE(UUID uuid) {
        return PLAYER().getUuid().equals(uuid);
    }

    protected boolean PASSIVE(GamePlayer player) {
        return PLAYER().equals(player);
    }

    public void register() {}

    public void unregister() {}
}
