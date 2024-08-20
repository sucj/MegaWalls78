package icu.suc.megawalls78.entity.custom.skill;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.util.Vector;

import java.util.Objects;

public class ShadowBurstSkull extends WitherSkull {

    private final float damage;

    public ShadowBurstSkull(Level world, Object owner, Object velocity, Object damage) {
        super(world, ((CraftLivingEntity) owner).getHandle(), new Vec3(((Vector) velocity).getX(), ((Vector) velocity).getY(), ((Vector) velocity).getZ()));
        this.damage = (float) damage;
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        Level world = this.level();

        if (world instanceof ServerLevel worldserver) {
            Entity entity = entityHitResult.getEntity();
            Entity entity1 = this.getOwner();
            boolean flag;

            if (entity1 instanceof LivingEntity entityliving) {
                DamageSource damagesource = this.damageSources().witherSkull(this, entityliving);

                flag = entity.hurt(damagesource, damage);
                if (flag) {
                    if (entity.isAlive()) {
                        EnchantmentHelper.doPostAttackEffects(worldserver, entity, damagesource);
                    } else {
                        entityliving.heal(5.0F, org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason.WITHER); // CraftBukkit
                    }
                }
            }
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        HitResult.Type movingobjectposition_enummovingobjecttype = hitResult.getType();

        if (movingobjectposition_enummovingobjecttype == HitResult.Type.ENTITY) {
            EntityHitResult movingobjectpositionentity = (EntityHitResult) hitResult;
            Entity entity = movingobjectpositionentity.getEntity();

            if (entity.getType().is(EntityTypeTags.REDIRECTABLE_PROJECTILE) && entity instanceof Projectile iprojectile) {

                iprojectile.deflect(ProjectileDeflection.AIM_DEFLECT, this.getOwner(), this.getOwner(), true);
            }

            this.onHitEntity(movingobjectpositionentity);
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, hitResult.getLocation(), GameEvent.Context.of(this, null));
        } else if (movingobjectposition_enummovingobjecttype == HitResult.Type.BLOCK) {
            BlockHitResult movingobjectpositionblock = (BlockHitResult) hitResult;

            this.onHitBlock(movingobjectpositionblock);
            BlockPos blockposition = movingobjectpositionblock.getBlockPos();

            this.level().gameEvent(GameEvent.PROJECTILE_LAND, blockposition, GameEvent.Context.of(this, this.level().getBlockState(blockposition)));
        }

        if (!this.level().isClientSide) {
            // CraftBukkit start
            // this.level().explode(this, this.getX(), this.getY(), this.getZ(), 1.0F, false, World.a.MOB);
            ExplosionPrimeEvent event = new ExplosionPrimeEvent(this.getBukkitEntity(), 2F, false);
            this.level().getCraftServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                this.level().explode(this, this.damageSources().witherSkull(this, getOwner()), icu.suc.megawalls78.util.Explosion.ONLY_BLOCK, this.getX(), this.getY(), this.getZ(), event.getRadius(), event.getFire(), Level.ExplosionInteraction.STANDARD, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION, SoundEvents.GENERIC_EXPLODE);
            }
            // CraftBukkit end
            this.discard(); // CraftBukkit - add Bukkit remove cause
        }
    }

    @Override
    public boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) && !Objects.equals(entity.getTeam(), getOwner().getTeam());
    }

    @Override
    public boolean ignoreExplosion(Explosion explosion) {
        return true;
    }
}
