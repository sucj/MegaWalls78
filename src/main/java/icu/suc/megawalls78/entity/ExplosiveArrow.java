package icu.suc.megawalls78.entity;

import icu.suc.megawalls78.util.DamageSource;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.PlayerUtil;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class ExplosiveArrow extends Arrow {

    private final float radius;
    private final float damage;

    public ExplosiveArrow(Level world, Object owner, Object radius, Object damage) {
        super(world, ((CraftLivingEntity) owner).getHandle(), ItemStack.EMPTY, null);
        this.radius = (float) radius;
        this.damage = (float) damage;
    }

    @Override
    public void tick() {
        super.tick();

        ((ServerLevel) level()).sendParticles(DustParticleOptions.REDSTONE, getX(), getY(), getZ(), 1, 0, 0, 0, 0);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        if (!hitResult.getType().equals(HitResult.Type.MISS)) {
            level().explode(this, Explosion.getDefaultDamageSource(level(), this), icu.suc.megawalls78.util.Explosion.ONLY_BLOCK, getX(), getY(), getZ(), radius, false, Level.ExplosionInteraction.TNT);
            Player player = (Player) getOwner().getBukkitEntity();
            for (Entity entity : EntityUtil.getNearbyEntities(this.getBukkitEntity(), radius)) {
                if (!(entity instanceof LivingEntity livingEntity)) {
                    continue;
                }
                if (PlayerUtil.isValidAllies(player, entity)) {
                    continue;
                }
                livingEntity.damage(damage, DamageSource.of(DamageType.PLAYER_EXPLOSION, player));
            }
            this.discard();
        }
    }

    public float getRadius() {
        return radius;
    }

    public float getDamage() {
        return damage;
    }
}
