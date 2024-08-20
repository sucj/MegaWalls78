package icu.suc.mw78.identity.todo.regular.blaze.skill;

import icu.suc.megawalls78.identity.trait.skill.Skill;
import org.bukkit.entity.Player;

public final class ImmolatingBurst extends Skill {

    public ImmolatingBurst() {
        super("immolating_burst", 100, 1000L);
    }

    @Override
    protected boolean use0(Player player) {
        return true;
    }
}
