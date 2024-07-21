package icu.suc.megawalls78.identity.impl.moleman.passive;

import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.Passive;
import net.kyori.adventure.text.Component;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Shortcut extends Passive implements IActionbar {

    private static final int MAX = 3;
    private static final PotionEffect SPEED = new PotionEffect(PotionEffectType.SPEED, 80, 1);
    private static final PotionEffect HASTE = new PotionEffect(PotionEffectType.HASTE, 80, 1);

    private int count;

    public Shortcut() {
        super("shortcut");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (shouldPassive(player) && Tag.MINEABLE_SHOVEL.isTagged(event.getBlock().getType())) {
            if (++count > MAX) {
                player.addPotionEffect(SPEED);
                player.addPotionEffect(HASTE);
                count = 0;
            }
        }
    }

    @Override
    public void unregister() {
        count = 0;
    }

    @Override
    public Component acbValue() {
        return Type.COMBO.accept(count, MAX);
    }
}
