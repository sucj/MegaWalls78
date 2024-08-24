package icu.suc.megawalls78.identity.trait.passive;

public abstract class TimerPassive extends Passive {

    protected long CURRENT() {
        return System.currentTimeMillis();
    }
}
