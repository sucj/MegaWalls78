package icu.suc.megawalls78.identity.impl.moleman.passive;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GameState;
import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.Passive;
import icu.suc.megawalls78.util.ItemBuilder;
import icu.suc.megawalls78.util.ItemUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class JunkFood extends Passive implements IActionbar {

    private static final int COOKIE_APPEAR = 50;
    private static final int PIE_APPEAR = 100;
    private static final int JUNK_APPLE_APPEAR = 300;
    private static final ItemBuilder COOKIE = ItemBuilder.of(Material.COOKIE)
            .setAmount(3)
            .addPrefix(Identity.MOLEMAN.getName().append(Component.space()))
            .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            .addPersistentData(ItemUtil.ID, PersistentDataType.STRING, ItemUtil.MOLEMAN_COOKIE);
    private static final ItemBuilder PIE = ItemBuilder.of(Material.PUMPKIN_PIE)
            .addPrefix(Identity.MOLEMAN.getName().append(Component.space()))
            .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            .addPersistentData(ItemUtil.ID, PersistentDataType.STRING, ItemUtil.MOLEMAN_PIE);
    private static final ItemBuilder JUNK_APPLE = ItemBuilder.of(Material.APPLE)
            .addPrefix(Identity.MOLEMAN.getName().append(Component.space()))
            .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            .addPersistentData(ItemUtil.ID, PersistentDataType.STRING, ItemUtil.MOLEMAN_JUNK_APPLE);
    private static final PotionEffect REGENERATION = new PotionEffect(PotionEffectType.REGENERATION, 100, 0, false);
    private static final int RECYCLE = 5;

    private int max = JUNK_APPLE_APPEAR;
    private int count = max;
    private int recycle = 1;

    public JunkFood() {
        super("junk_food");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        if (shouldPassive(player) && isAvailable() && Tag.MINEABLE_SHOVEL.isTagged(event.getBlock().getType())) {
            if (++count > max) {
                if (count > JUNK_APPLE_APPEAR) {
                    player.getInventory().addItem(JUNK_APPLE.build());
                    max = COOKIE_APPEAR;
                    count = 1;
                } else if (count > PIE_APPEAR) {
                    player.getInventory().addItem(PIE.build());
                    max = JUNK_APPLE_APPEAR;
                } else if (count > COOKIE_APPEAR) {
                    player.getInventory().addItem(COOKIE.build());
                    max = PIE_APPEAR;
                }
            }
        }
    }

    @EventHandler
    public void onEatApple(PlayerItemConsumeEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        if (shouldPassive(player)) {
            ItemStack itemStack = event.getItem();
            boolean isJunk = ItemUtil.isMW78Item(itemStack, ItemUtil.MOLEMAN_JUNK_APPLE);
            if (isJunk || itemStack.getType().equals(Material.GOLDEN_APPLE)) {
                if (++this.recycle > RECYCLE) {
                    event.setReplacement(itemStack);
                    this.recycle = 1;
                }
                if (isJunk) {
                    player.addPotionEffect(REGENERATION);
                }
            }
        }
    }

    @Override
    public void unregister() {

    }

    @Override
    public Component acb() {
        return Type.COMBO_STATE.accept(count, max, isAvailable());
    }

    private boolean isAvailable() {
        return MegaWalls78.getInstance().getGameManager().getState().equals(GameState.PREPARING);
    }
}
