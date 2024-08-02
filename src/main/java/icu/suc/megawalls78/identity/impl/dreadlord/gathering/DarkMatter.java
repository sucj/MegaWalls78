package icu.suc.megawalls78.identity.impl.dreadlord.gathering;

import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.passive.ChargePassive;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class DarkMatter extends Gathering {

    private static final Set<Material> ORES = Set.of(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE);
    private static final List<Material> ARMORS = List.of(Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS);

    public DarkMatter() {
        super("dark_matter", Internal.class);
    }

    public static final class Internal extends ChargePassive {

        private int armor;

        public Internal() {
            super("dark_matter", 20);
        }

        @EventHandler
        public void onBreakBlock(BlockDropItemEvent event) {
            if (event.isCancelled()) {
                return;
            }
            Player player = event.getPlayer();
            BlockState blockState = event.getBlockState();
            if (PASSIVE(player) && condition(blockState)) {
                handle_smelt(event);
                if (CHARGE()) {
                    handle_armor(event, player, blockState.getLocation(), ARMORS.get(armor));
                    if (++armor >= ARMORS.size()) {
                        armor = 0;
                    }
                    CHARGE_RESET();
                }
            }
        }

        private static boolean condition(BlockState blockState) {
            return ORES.contains(blockState.getType());
        }

        private static void handle_smelt(BlockDropItemEvent event) {
            for (Item item : event.getItems()) {
                ItemStack itemStack = item.getItemStack();
                if (itemStack.getType().equals(Material.RAW_IRON)) {
                    item.setItemStack(ItemStack.of(Material.IRON_INGOT, itemStack.getAmount()));
                }
            }
        }

        private static void handle_armor(BlockDropItemEvent event, Player player, Location location, Material material) {
            event.getItems().add(player.getWorld().dropItemNaturally(location, ItemStack.of(material)));
        }
    }
}
