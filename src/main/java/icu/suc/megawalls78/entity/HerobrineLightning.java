package icu.suc.megawalls78.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.entity.CraftPlayer;

public class HerobrineLightning extends LightningBolt {

  private Player target;

  public HerobrineLightning(Level world) {
    super(EntityType.LIGHTNING_BOLT, world);
  }

  @Override
  public void tick() { //tick()
    --this.life; // life

    if (this.life < 0) {
      this.discard();
    }

    if (this.life >= 0) {
      if (target != null) {
        target.thunderHit(((ServerLevel) this.level()), this); // thunderHit(level(), this)
      }
    }
  }

  public void setTarget(org.bukkit.entity.Player target) {
    this.target = ((CraftPlayer) target).getHandle();
  }
}
