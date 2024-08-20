package icu.suc.megawalls78.entity.custom.arrow;

import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;

public abstract class NMSArrow extends Arrow {

    public NMSArrow(Level world, double x, double y, double z, Object stack, Object shotFrom) {
        super(world, x, y, z, ((CraftItemStack) stack).handle, shotFrom == null ? null : ((CraftItemStack) shotFrom).handle);
    }

    public NMSArrow(Level world, double x, double y, double z, Object stack) {
        super(world, x, y, z, ((CraftItemStack) stack).handle, null);
    }

    public NMSArrow(Level world, double x, double y, double z) {
        super(world, x, y, z, ItemStack.EMPTY, null);
    }

    public NMSArrow(Level world, Object owner, Object stack, Object shotFrom) {
        super(world, ((CraftLivingEntity) owner).getHandle(), ((CraftItemStack) stack).handle, shotFrom == null ? null : ((CraftItemStack) shotFrom).handle);
    }

    public NMSArrow(Level world, Object owner, Object stack) {
        super(world, ((CraftLivingEntity) owner).getHandle(), ((CraftItemStack) stack).handle, null);
    }

    public NMSArrow(Level world, Object owner) {
        super(world, ((CraftLivingEntity) owner).getHandle(), ItemStack.EMPTY, null);
    }
}
