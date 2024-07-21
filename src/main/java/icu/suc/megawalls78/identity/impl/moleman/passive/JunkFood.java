package icu.suc.megawalls78.identity.impl.moleman.passive;

import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.Passive;
import net.kyori.adventure.text.Component;

public class JunkFood extends Passive implements IActionbar {

    public JunkFood() {
        super("junk_food");
    }

    @Override
    public void unregister() {

    }

    @Override
    public Component acb() {
        return null;
    }
}
