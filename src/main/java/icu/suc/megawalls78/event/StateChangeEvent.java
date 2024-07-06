package icu.suc.megawalls78.event;

import icu.suc.megawalls78.game.GameState;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class StateChangeEvent extends Event {
    private final GameState state;

    private static final HandlerList handlers = new HandlerList();

    public StateChangeEvent(GameState state) {
        this.state = state;
    }

    public GameState getState() {
        return state;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
