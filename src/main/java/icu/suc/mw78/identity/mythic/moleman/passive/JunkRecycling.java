package icu.suc.mw78.identity.mythic.moleman.passive;

import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.passive.ChargePassive;
import icu.suc.megawalls78.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

@Trait("junk_recycling")
public class JunkRecycling extends ChargePassive {

    public JunkRecycling() {
        super(5);
    }

    @EventHandler(ignoreCancelled = true)
    public void onConsumeApple(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();
        if (PASSIVE(player) && condition(itemStack) && CHARGE()) {
            event.setReplacement(itemStack);
            CHARGE_RESET();
        }
    }

    private static boolean condition(ItemStack itemStack) {
        return ItemUtil.isMW78Item(itemStack, ItemUtil.MOLEMAN_JUNK_APPLE) || itemStack.getType().equals(Material.GOLDEN_APPLE);
    }
}
