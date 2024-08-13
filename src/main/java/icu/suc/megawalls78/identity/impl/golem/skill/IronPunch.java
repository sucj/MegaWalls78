package icu.suc.megawalls78.identity.impl.golem.skill;

import icu.suc.megawalls78.identity.trait.skill.Skill;
import org.bukkit.entity.Player;

public final class IronPunch extends Skill {

    public IronPunch() {
        super("iron_punch", 100, 2000L);
    }

    @Override
    protected boolean use0(Player player) {
        return true;
    }
}
