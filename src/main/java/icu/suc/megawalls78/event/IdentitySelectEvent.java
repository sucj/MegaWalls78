package icu.suc.megawalls78.event;

import icu.suc.megawalls78.identity.Identity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class IdentitySelectEvent extends Event {

  private final UUID uuid;
  private Identity identity;

  private static final HandlerList handlers = new HandlerList();

  protected IdentitySelectEvent(UUID uuid, Identity identity) {
    this.uuid = uuid;
    this.identity = identity;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
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
  }

  public static class Post extends IdentitySelectEvent {
    public Post(UUID uuid, Identity identity) {
      super(uuid, identity);
    }
  }
}
