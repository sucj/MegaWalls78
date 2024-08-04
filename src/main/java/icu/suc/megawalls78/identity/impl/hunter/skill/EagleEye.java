package icu.suc.megawalls78.identity.impl.hunter.skill;

import icu.suc.megawalls78.identity.trait.skill.Skill;
import org.bukkit.entity.Player;

public final class EagleEye extends Skill {

    public EagleEye() {
        super("eagle_eye", 100, 1000L);
    }

    @Override
    protected boolean use0(Player player) {
        return true;
    }
}
