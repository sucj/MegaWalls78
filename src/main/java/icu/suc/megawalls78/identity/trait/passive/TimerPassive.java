package icu.suc.megawalls78.identity.trait.passive;

public abstract class TimerPassive extends Passive {

    public TimerPassive(String id) {
        super(id);
    }

    protected long CURRENT() {
        return System.currentTimeMillis();
    }
}
