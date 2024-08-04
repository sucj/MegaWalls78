package icu.suc.megawalls78.identity.impl.skeleton.skill;

import icu.suc.megawalls78.identity.trait.skill.Skill;
import icu.suc.megawalls78.util.EntityUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class ExplosiveArrow extends Skill {

    private static final float RADIUS = 6.0F;
    private static final float DAMAGE = 6.0F;

    public ExplosiveArrow() {
        super("explosive_arrow", 100, 1000L);
    }

    @Override
    protected boolean use0(Player player) {
        Location location = player.getEyeLocation();
        EntityUtil.spawn(location, EntityUtil.Type.EXPLOSIVE_ARROW, entity -> entity.setVelocity(location.getDirection().multiply(1.5D)), player, RADIUS, DAMAGE);
        return true;
    }
}
