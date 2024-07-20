package icu.suc.megawalls78.identity.trait;

import icu.suc.megawalls78.game.GamePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.UUID;

public abstract class Passive extends Trait implements Listener {

    private GamePlayer player;

    public Passive(String id) {
        super(id, Component.translatable("mw78.passive." + id));
    }

    protected boolean shouldPassive(Player player) {
        return player != null && shouldPassive(player.getUniqueId());
    }

    protected boolean shouldPassive(UUID uuid) {
        return this.player.getUuid().equals(uuid);
    }

    public abstract void unregister();

    public GamePlayer getPlayer() {
        return player;
    }

    public void setPlayer(GamePlayer player) {
        this.player = player;
    }
}
