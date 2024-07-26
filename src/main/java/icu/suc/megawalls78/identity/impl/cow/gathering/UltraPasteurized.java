package icu.suc.megawalls78.identity.impl.cow.gathering;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GameState;
import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.passive.ChargePassive;
import icu.suc.megawalls78.util.*;
import icu.suc.megawalls78.util.Effect;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public final class UltraPasteurized extends Gathering {

    private static final ItemBuilder MILK = ItemBuilder.of(Material.MILK_BUCKET)
            .setAmount(2)
            .addPrefix(Identity.COW.getName().append(Component.space()))
            .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            .setMaxStackSize(64)
            .addPersistentData(ItemUtil.ID, PersistentDataType.STRING, ItemUtil.COW_MILK);

    private static final Effect<Location> EFFECT_SKILL = Effect.create(location -> location.getWorld().playSound(location, Sound.BLOCK_WET_SPONGE_DRIES, SoundCategory.BLOCKS, 1.0F, 1.0F));

    public UltraPasteurized() {
        super("ultra_pasteurized", Internal.class);
    }

    public static final class Internal extends ChargePassive implements IActionbar {

        public Internal() {
            super("ultra_pasteurized", 60);
        }

        @EventHandler
        public void brokenBlock(BlockDropItemEvent event) {
            if (event.isCancelled()) {
                return;
            }
            Player player = event.getPlayer();
            BlockState blockState = event.getBlockState();
            if (PASSIVE(player) && condition_available() && condition_stone(blockState) && CHARGE()) {
                handle(event.getItems(), player, blockState.getLocation());
                CHARGE_RESET();
            }
        }

        private static void handle(List<Item> items, Player player, Location location) {
            items.add(player.getWorld().dropItemNaturally(location, MILK.build()));
            EFFECT_SKILL.play(location);
        }

        private static boolean condition_stone(BlockState blockState) {
            return BlockUtil.isStone(blockState.getType());
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
