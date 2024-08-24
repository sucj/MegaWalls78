package icu.suc.mw78.identity.mythic.moleman.passive;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GameState;
import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.passive.ChargePassive;
import icu.suc.megawalls78.util.InventoryUtil;
import icu.suc.megawalls78.util.ItemBuilder;
import icu.suc.megawalls78.util.ItemUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

@Trait("junk_food")
public final class JunkFood extends ChargePassive implements IActionbar {

    private static final int COOKIE_APPEAR = 50;
    private static final int PIE_APPEAR = 100;
    private static final int JUNK_APPLE_APPEAR = 300;

    private static final ItemBuilder COOKIE = ItemBuilder.of(Material.COOKIE)
            .setAmount(3)
            .addPrefix(Identity.MOLEMAN.getName().append(Component.space()))
            .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            .setMW78Id(ItemUtil.MOLEMAN_COOKIE);
    private static final ItemBuilder PIE = ItemBuilder.of(Material.PUMPKIN_PIE)
            .addPrefix(Identity.MOLEMAN.getName().append(Component.space()))
            .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            .setMW78Id(ItemUtil.MOLEMAN_PIE);
    private static final ItemBuilder JUNK_APPLE = ItemBuilder.of(Material.APPLE)
            .addPrefix(Identity.MOLEMAN.getName().append(Component.space()))
            .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            .setMW78Id(ItemUtil.MOLEMAN_JUNK_APPLE);

    public JunkFood() {
        super(JUNK_APPLE_APPEAR);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockDropItemEvent event) {
        Player player = event.getPlayer();
        if (PASSIVE(player) && condition_available() && condition_shovelable(event) && CHARGE()) {
            if (CHARGE_COUNT() > JUNK_APPLE_APPEAR) {
                handle(event, JUNK_APPLE.build());
                CHARGE = COOKIE_APPEAR;
                CHARGE_RESET();
            } else if (CHARGE_COUNT() > PIE_APPEAR) {
                handle(event, PIE.build());
                CHARGE = JUNK_APPLE_APPEAR;
            } else if (CHARGE_COUNT() > COOKIE_APPEAR) {
                handle(event, COOKIE.build());
                CHARGE = PIE_APPEAR;
            }
        }
    }

    private static boolean condition_shovelable(BlockDropItemEvent event) {
        return Tag.MINEABLE_SHOVEL.isTagged(event.getBlockState().getType());
    }

    private static boolean condition_available() {
        return MegaWalls78.getInstance().getGameManager().getState().equals(GameState.PREPARING);
    }

    private static void handle(BlockDropItemEvent event, ItemStack itemStack) {
        InventoryUtil.addItem(event.getPlayer(), event, itemStack);
    }

    @Override
    public Component acb() {
        return Type.CHARGE_STATE.accept(CHARGE_COUNT(), CHARGE, condition_available());
    }
}
