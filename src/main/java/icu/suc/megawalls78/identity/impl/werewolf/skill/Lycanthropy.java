package icu.suc.megawalls78.identity.impl.werewolf.skill;

import icu.suc.megawalls78.identity.trait.Skill;
import org.bukkit.entity.Player;

public final class Lycanthropy extends Skill {

    public Lycanthropy() {
        super("lycanthropy", 100, 6000L);
    }

    @Override
    protected boolean use0(Player player) {
        return true;
    }
}
