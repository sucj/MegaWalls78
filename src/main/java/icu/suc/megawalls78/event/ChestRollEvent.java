package icu.suc.megawalls78.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public abstract class ChestRollEvent extends Event {

    private final Player player;
    private final Block block;

    protected ChestRollEvent(Player player, Block block) {
        this.player = player;
        this.block = block;
    }

    public Player getPlayer() {
        return player;
    }

    public Block getBlock() {
        return block;
    }

    public static class Pre extends ChestRollEvent implements Cancellable {

        private static final HandlerList handlers = new HandlerList();

        private double probability;

        private boolean cancelled;

        public Pre(Player player, Block block, double probability) {
            super(player, block);
            this.probability = probability;
        }

        public double getProbability() {
            return probability;
        }

        public void setProbability(double probability) {
            this.probability = probability;
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

    public static class Post extends ChestRollEvent {

        private static final HandlerList handlers = new HandlerList();

        private final Inventory inventory;

        public Post(Player player, Block block, Inventory inventory) {
            super(player, block);
            this.inventory = inventory;
        }

        public Inventory getInventory() {
            return inventory;
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
