package icu.suc.megawalls78.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class IncreaseStatsEvent extends Event implements Cancellable {

    private final UUID uuid;
    private final boolean isFinal;

    private boolean cancelled;

    protected IncreaseStatsEvent(UUID uuid, boolean isFinal) {
        this.uuid = uuid;
        this.isFinal = isFinal;
    }

    public UUID getUuid() {
        return uuid;
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

        private static final HandlerList handlers = new HandlerList();

        public Kill(UUID uuid, boolean isFinal) {
            super(uuid, isFinal);
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

        public Death(UUID uuid, boolean isFinal) {
            super(uuid, isFinal);
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

        public Assist(UUID uuid, boolean isFinal) {
            super(uuid, isFinal);
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
