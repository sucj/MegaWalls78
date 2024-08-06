package icu.suc.megawalls78.event;

import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public abstract class ChestRollEvent extends PlayerEvent {

    private final BlockState blockState;

    protected ChestRollEvent(Player player, BlockState blockState) {
        super(player);
        this.blockState = blockState;
    }

    public BlockState getBlockState() {
        return blockState;
    }

    public static class Pre extends ChestRollEvent implements Cancellable {

        private static final HandlerList handlers = new HandlerList();

        private double chance;

        private boolean cancelled;

        public Pre(Player player, BlockState blockState, double chance) {
            super(player, blockState);
            this.chance = chance;
        }

        public double getChance() {
            return chance;
        }

        public void setChance(double chance) {
            this.chance = chance;
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

        public Post(Player player, BlockState blockState, Inventory inventory) {
            super(player, blockState);
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
