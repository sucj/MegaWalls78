package icu.suc.megawalls78.event;

import icu.suc.megawalls78.game.GamePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

public abstract class IncreaseStatsEvent extends Event implements Cancellable {

    private final GamePlayer player;
    private final boolean isFinal;

    private boolean cancelled;

    protected IncreaseStatsEvent(GamePlayer player, boolean isFinal) {
        this.player = player;
        this.isFinal = isFinal;
    }

    public GamePlayer getPlayer() {
        return player;
    }

    public boolean isFinal() {
        return isFinal;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    public static class Kill extends IncreaseStatsEvent {

        private final PlayerDeathEvent event;

        private static final HandlerList handlers = new HandlerList();

        public Kill(GamePlayer player, boolean isFinal, PlayerDeathEvent event) {
            super(player, isFinal);
            this.event = event;
        }

        public PlayerDeathEvent getEvent() {
            return event;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            return handlers;
        }

        public static HandlerList getHandlerList() {
            return handlers;
        }
    }

    public static class Death extends IncreaseStatsEvent {

        private static final HandlerList handlers = new HandlerList();

        public Death(GamePlayer player, boolean isFinal) {
            super(player, isFinal);
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            return handlers;
        }

        public static HandlerList getHandlerList() {
            return handlers;
        }
    }

    public static class Assist extends IncreaseStatsEvent {

        private static final HandlerList handlers = new HandlerList();

        public Assist(GamePlayer player, boolean isFinal) {
            super(player, isFinal);
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            return handlers;
        }

        public static HandlerList getHandlerList() {
            return handlers;
        }
    }
}
