package icu.suc.megawalls78.identity.impl.cow.gathering;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GameState;
import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.passive.ChargePassive;
import icu.suc.megawalls78.util.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;

public final class UltraPasteurized extends Gathering {

    private static final ItemBuilder MILK = ItemBuilder.of(Material.MILK_BUCKET)
            .setAmount(2)
            .addPrefix(Identity.COW.getName().append(Component.space()))
            .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            .setMaxStackSize(64)
            .setMW78Id(ItemUtil.COW_MILK);

    private static final Effect<Location> EFFECT_SKILL = Effect.create(location -> location.getWorld().playSound(location, Sound.BLOCK_WET_SPONGE_DRIES, SoundCategory.BLOCKS, 1.0F, 1.0F));

    public UltraPasteurized() {
        super("ultra_pasteurized", Internal.class);
    }

    public static final class Internal extends ChargePassive {

        public Internal() {
            super("ultra_pasteurized", 60);
        }

        @EventHandler(ignoreCancelled = true)
        public void onBreakBlock(BlockDropItemEvent event) {
            Player player = event.getPlayer();
            if (PASSIVE(player) && condition_available() && condition_stone(event) && CHARGE()) {
                handle(event);
                CHARGE_RESET();
            }
        }

        private static void handle(BlockDropItemEvent event) {
            InventoryUtil.addItem(event.getPlayer(), event, MILK.build());
            EFFECT_SKILL.play(event.getBlockState().getLocation());
        }

        private static boolean condition_stone(BlockDropItemEvent event) {
            return BlockUtil.isStone(event.getBlockState().getType());
        }

        private static boolean condition_available() {
            return MegaWalls78.getInstance().getGameManager().getState().equals(GameState.PREPARING);
        }

        @Override
        public Component acb() {
            return Type.CHARGE_STATE.accept(CHARGE_COUNT(), CHARGE, condition_available());
        }
    }
}
