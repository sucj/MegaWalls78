package icu.suc.megawalls78.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class BlockUtil {
    public static final Set<Material> STONES = Set.of(Material.STONE, Material.DEEPSLATE);
    public static final Set<Material> WOODS = Set.of(Material.OAK_WOOD, Material.SPRUCE_WOOD, Material.BIRCH_WOOD, Material.JUNGLE_WOOD, Material.ACACIA_WOOD, Material.CHERRY_WOOD, Material.DARK_OAK_WOOD, Material.MANGROVE_WOOD, Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG, Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.CHERRY_LOG, Material.DARK_OAK_LOG, Material.MANGROVE_LOG);
    public static final Set<Material> ORES = Set.of(Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE, Material.DIAMOND_ORE, Material.COPPER_ORE, Material.EMERALD_ORE, Material.LAPIS_ORE, Material.REDSTONE_ORE, Material.NETHER_GOLD_ORE, Material.NETHER_QUARTZ_ORE, Material.DEEPSLATE_COAL_ORE, Material.DEEPSLATE_IRON_ORE, Material.DEEPSLATE_GOLD_ORE, Material.DEEPSLATE_DIAMOND_ORE, Material.DEEPSLATE_COPPER_ORE, Material.DEEPSLATE_EMERALD_ORE, Material.DEEPSLATE_LAPIS_ORE, Material.DEEPSLATE_REDSTONE_ORE);
    public static final Set<Material> DIRT = Set.of(Material.DIRT, Material.COARSE_DIRT, Material.ROOTED_DIRT, Material.SAND, Material.RED_SAND, Material.MUD, Material.CLAY, Material.GRAVEL);

    public static boolean isStone(Material material) {
        return STONES.contains(material);
    }

    public static boolean isWood(Material material) {
        return WOODS.contains(material);
    }

    public static boolean isOre(Material material) {
        return ORES.contains(material);
    }

    public static boolean isDirt(Material material) {
        return DIRT.contains(material);
    }

    public static boolean isNatural(Material material) {
        return isStone(material) || isWood(material) || isOre(material) || isDirt(material);
    }

    public static boolean breakNaturally(Block block) {
        return block.breakNaturally();
    }

    public static boolean breakNaturallyNoDrops(Block block) {
        net.minecraft.world.level.block.state.BlockState iblockdata = ((CraftBlock) block).getNMS();
        net.minecraft.world.level.block.Block mcBlock = iblockdata.getBlock();
        boolean result = false;
        LevelAccessor world = ((CraftBlock) block).getCraftWorld().getHandle();
        BlockPos position = ((CraftBlock) block).getPosition();
        if (mcBlock != Blocks.AIR) {
            if (iblockdata.getBlock() instanceof net.minecraft.world.level.block.BaseFireBlock) {
                world.levelEvent(net.minecraft.world.level.block.LevelEvent.SOUND_EXTINGUISH_FIRE, position, 0);
            } else {
                world.levelEvent(net.minecraft.world.level.block.LevelEvent.PARTICLES_DESTROY_BLOCK, position, net.minecraft.world.level.block.Block.getId(iblockdata));
            }
            result = true;
        }
        boolean destroyed = world.removeBlock(position, false);
        if (destroyed) {
            mcBlock.destroy(world, position, iblockdata);
        }
        if (result) {
            if (mcBlock instanceof net.minecraft.world.level.block.IceBlock iceBlock) {
                iceBlock.afterDestroy(world.getMinecraftWorld(), position, net.minecraft.world.item.ItemStack.EMPTY);
            } else if (mcBlock instanceof net.minecraft.world.level.block.TurtleEggBlock turtleEggBlock) {
                turtleEggBlock.decreaseEggs(world.getMinecraftWorld(), position, iblockdata);
            }
        }
        return destroyed && result;
    }

    public static boolean isDestroyable(Block block) {
        return ((CraftBlock) block).getNMS().isDestroyable();
    }

    public static boolean canInteract(Block block) {
        if (block == null) {
            return false;
        }
        net.minecraft.world.level.block.Block nms = ((CraftBlock) block).getNMS().getBlock();
        try {
            nms.getClass().getDeclaredMethod("useWithoutItem", BlockState.class, Level.class, BlockPos.class, Player.class, BlockHitResult.class);
            return true;
        } catch (NoSuchMethodException ignored) {
        }
        try {
            nms.getClass().getDeclaredMethod("useItemOn", net.minecraft.world.item.ItemStack.class, BlockState.class, Level.class, BlockPos.class, Player.class, InteractionHand.class, BlockHitResult.class);
            return true;
        }
        catch (NoSuchMethodException ignored) {
        }
        return false;
    }

    public static void addDrops(BlockDropItemEvent event, ItemStack itemStack) {
        Location location = event.getBlockState().getLocation();
        event.getItems().add(location.getWorld().dropItemNaturally(location, itemStack));
    }
}
