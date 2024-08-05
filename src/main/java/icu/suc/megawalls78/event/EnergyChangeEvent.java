package icu.suc.megawalls78.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class EnergyChangeEvent extends PlayerEvent implements Cancellable {

    private final float origin;
    private final float energy;
    private final float max;

    private boolean cancelled;

    private static final HandlerList handlers = new HandlerList();

    public EnergyChangeEvent(Player player, float origin, float energy, float max) {
        super(player);
        this.origin = origin;
        this.energy = energy;
        this.max = max;
    }

    public float getOrigin() {
        return origin;
    }

    public float getEnergy() {
        return energy;
    }

    public float getMax() {
        return max;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
