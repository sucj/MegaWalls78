package icu.suc.megawalls78.identity.trait;

import icu.suc.megawalls78.util.Formatters;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public interface IActionbar {

    Component A = Component.translatable("mw78.activate", NamedTextColor.GREEN);
    Component D = Component.translatable("mw78.deactivate", NamedTextColor.RED);

    Component acbValue();

    enum Type {
        COOLDOWN,
        DURATION,
        COOLDOWN_DURATION,
        COMBO,
        CHARGE;

        public Component accept(double... args) {
            switch (this) {
                case COOLDOWN -> {
                    double cooldown = Math.max((args[2] - args[0] + args[1]) / 1000, 0);
                    return cooldown == 0 ? A : Component.text(Formatters.NUMBER_Z.format(cooldown), NamedTextColor.RED);
                }
                case DURATION -> {
                    double duration = Math.max((args[2] - args[0] + args[1]) / 1000, 0);
                    return duration == 0 ? D : Component.text(Formatters.NUMBER_Z.format(duration), NamedTextColor.GREEN);
                }
                case COOLDOWN_DURATION -> {
                    double cooldown = Math.max((args[2] - args[0] + args[1]) / 1000, 0);
                    double duration = Math.max(args[3] / 1000, 0);
                    return duration == 0 ? (cooldown == 0 ? A : Component.text(Formatters.NUMBER_Z.format(cooldown), NamedTextColor.RED)) : Component.text(Formatters.NUMBER_Z.format(duration), NamedTextColor.GREEN);
                }
                case COMBO -> {
                    return args[0] >= args[1] ? A : Component.text(Formatters.NUMBER.format(args[0]), NamedTextColor.GREEN);
                }
                case CHARGE -> {
                    return null;
                }
            }
            return null;
        }
    }
}
