package icu.suc.mw78.identity.regular.dreadlord.skill;

import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.skill.Skill;
import icu.suc.megawalls78.util.Effect;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.RandomUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Trait("shadow_burst")
public final class ShadowBurst extends Skill {

    private static final float DAMAGE = 8.0F;

    private static final Effect<Location> EFFECT_SKILL = Effect.create(location -> location.getWorld().playSound(location, Sound.ENTITY_WITHER_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F, 0));

    public ShadowBurst() {
        super(100, 1000L);
    }

    @Override
    protected boolean use0(Player player) {
        Location location = player.getEyeLocation();
        EFFECT_SKILL.play(location);
        Vector direction = location.getDirection();
        for (int i = 0; i < 3; i++) {
            float h = (float) i / 10;
            h = h == 0 ? 0 : RandomUtil.RANDOM.nextFloat(h);
            float v = (float) i / 100;
            v = v == 0 ? 0 : RandomUtil.RANDOM.nextFloat(v);
            Vector vector = direction.add(new Vector(h, v, h));
            EntityUtil.spawn(location, EntityUtil.Type.SHADOW_BURST_SKULL, null, player, vector, DAMAGE);
        }
        return true;
    }
}
