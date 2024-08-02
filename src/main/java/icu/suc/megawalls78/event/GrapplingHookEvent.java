package icu.suc.megawalls78.event;

import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GrapplingHookEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final FishHook hook;
    private final State state;
    private final ItemStack itemStack;

    private boolean cancelled;

    public GrapplingHookEvent(Player player, FishHook hook, State state, ItemStack itemStack) {
        super(player);
        this.hook = hook;
        this.state = state;
        this.itemStack = itemStack;
    }

    public FishHook getHook() {
        return hook;
    }

    public State getState() {
        return state;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    public static enum State {
        CAST, PULL
    }
}
