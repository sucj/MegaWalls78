package icu.suc.megawalls78.identity.trait;

import icu.suc.megawalls78.util.Formatters;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;

public interface IActionbar {

    Component acb();

    enum Type {
        STATE,
        COOLDOWN,
        COOLDOWN_STATE,
        DURATION,
        DURATION_COOLDOWN,
        DURATION_COOLDOWN_STATE,
        CHARGE,
        CHARGE_COOLDOWN,
        CHARGE_STATE,
        MODE;

        private static final Component A = Component.translatable("mw78.actionbar.activate", NamedTextColor.GREEN);
        private static final Component D = Component.translatable("mw78.actionbar.deactivate", NamedTextColor.RED);
        private static final TranslatableComponent C = Component.translatable("mw78.actionbar.charge", NamedTextColor.DARK_GRAY);

        public static final double SECOND_MILLS_D = 1000.0D;

        public Component accept(Object... args) {
            switch (this) {
                case STATE -> {
                    boolean state = (boolean) args[0];

                    return state ? A : D;
                }
                case COOLDOWN -> {
                    long cooldown = ((long) args[0]);

                    double cd = Math.max(cooldown, 0) / SECOND_MILLS_D;

                    return cd == 0 ? A : cooldown(cd);
                }
                case COOLDOWN_STATE -> {
                    long cooldown = ((long) args[0]);
                    boolean state = (boolean) args[1];

                    double cd = Math.max(cooldown, 0) / SECOND_MILLS_D;

                    return cd == 0 ? state ? A : D : cooldown(cd);
                }
                case DURATION -> {
                    long duration = ((long) args[0]);

                    double remain = Math.max(duration, 0) / SECOND_MILLS_D;

                    return remain == 0 ? D : duration(remain);
                }
                case DURATION_COOLDOWN -> {
                    long cooldown = ((long) args[0]);
                    long duration = ((long) args[1]);

                    double cd = Math.max(cooldown, 0) / SECOND_MILLS_D;
                    double remain = Math.max(duration, 0) / SECOND_MILLS_D;

                    return remain == 0 ? (cd == 0 ? A : cooldown(cd)) : duration(remain);
                }
                case DURATION_COOLDOWN_STATE -> {
                    long cooldown = ((long) args[0]);
                    long duration = ((long) args[1]);
                    boolean state = (boolean) args[2];

                    double cd = Math.max(cooldown, 0) / SECOND_MILLS_D;
                    double remain = Math.max(duration, 0) / SECOND_MILLS_D;

                    return remain == 0 ? cd == 0 ? state ? A : D : cooldown(cd) : duration(remain);
                }
                case CHARGE -> {
                    int count = (int) args[0];
                    int charge = (int) args[1];

                    return count >= charge ? A : charge(count, charge);
                }
                case CHARGE_COOLDOWN -> {
                    long cooldown = ((long) args[0]);
                    int count = (int) args[1];
                    int charge = (int) args[2];

                    double cd = Math.max(cooldown, 0) / SECOND_MILLS_D;

                    return cd == 0 ? (count >= charge ? A : charge(count, charge)) : cooldown(cd);
                }
                case CHARGE_STATE -> {
                    int count = (int) args[0];
                    int charge = (int) args[1];
                    boolean state = (boolean) args[2];

                    return state ? count >= charge ? A : charge(count, charge) : D;
                }
                case MODE -> {
                    Component mode = (Component) args[0];

                    return mode(mode);
                }
            }
            return null;
        }

        private static Component cooldown(double cooldown) {
            return Component.text(Formatters.NUMBER_Z.format(cooldown), NamedTextColor.RED);
        }

        private static Component duration(double duration) {
            return Component.text(Formatters.NUMBER_Z.format(duration), NamedTextColor.GREEN);
        }

        private static Component charge(int count, int max) {
            return C.arguments(Component.text(count, NamedTextColor.GRAY), Component.text(max, NamedTextColor.GREEN));
        }

        private static Component mode(Component mode) {
            return mode.color(NamedTextColor.GREEN);
        }
    }
}
