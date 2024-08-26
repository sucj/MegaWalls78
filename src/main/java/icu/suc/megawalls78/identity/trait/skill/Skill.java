package icu.suc.megawalls78.identity.trait.skill;

import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.Trait;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.management.TraitManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public abstract class Skill extends Trait implements IActionbar {

    long COOLDOWN_LAST;

    private Passive passive;

    public boolean use(Player player) {
        if (!available()) {
            return false;
        }
        if (COOLDOWN()) {
            if (use0(player)) {
                COOLDOWN_RESET();
                return true;
            }
        }
        return false;
    }

    protected boolean available() {
        return PLAYER().getEnergy() >= getCost();
    }

    @Override
    public Component acb() {
        return Type.COOLDOWN_STATE.accept(COOLDOWN_REMAIN(), available());
    }

    protected abstract boolean use0(Player player);

    public float getCost() {
        return TraitManager.cost(getClass());
    }

    public long getCooldown() {
        return TraitManager.cooldown(getClass());
    }

    public Class<? extends Passive> getInternal() {
        return TraitManager.internal(getClass());
    }

    public Passive getPassive() {
        return passive;
    }

    public void setPassive(Passive passive) {
        this.passive = passive;
    }

    protected long CURRENT() {
        return System.currentTimeMillis();
    }

    protected boolean COOLDOWN() {
        return CURRENT() - COOLDOWN_LAST() >= getCooldown();
    }

    protected long COOLDOWN(long delta) {
        return COOLDOWN_LAST += delta;
    }

    protected long COOLDOWN_LAST() {
        return COOLDOWN_LAST;
    }

    protected void COOLDOWN_RESET() {
        COOLDOWN_LAST = CURRENT();
    }

    protected void COOLDOWN_END() {
        COOLDOWN_LAST = 0;
    }

    protected long COOLDOWN_REMAIN() {
        return getCooldown() - CURRENT() + COOLDOWN_LAST();
    }
}
