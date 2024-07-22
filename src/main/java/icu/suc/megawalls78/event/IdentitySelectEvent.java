package icu.suc.megawalls78.event;

import icu.suc.megawalls78.identity.Identity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class IdentitySelectEvent extends Event {

    private final UUID uuid;
    private Identity identity;

    protected IdentitySelectEvent(UUID uuid, Identity identity) {
        this.uuid = uuid;
        this.identity = identity;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    public static class Pre extends IdentitySelectEvent implements Cancellable {

        private static final HandlerList handlers = new HandlerList();

        private boolean cancelled;

        public Pre(UUID uuid, Identity identity) {
            super(uuid, identity);
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

    public static class Post extends IdentitySelectEvent {

        private static final HandlerList handlers = new HandlerList();

        public Post(UUID uuid, Identity identity) {
            super(uuid, identity);
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
