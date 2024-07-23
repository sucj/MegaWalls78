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
        DURATION,
        DURATION_COOLDOWN,
        COMBO,
        COMBO_COOLDOWN,
        COMBO_STATE,
        MODE;

        private static final Component A = Component.translatable("mw78.actionbar.activate", NamedTextColor.GREEN);
        private static final Component D = Component.translatable("mw78.actionbar.deactivate", NamedTextColor.RED);
        private static final TranslatableComponent C = Component.translatable("mw78.actionbar.combo", NamedTextColor.DARK_GRAY);

        private static final float SECOND_MILLS_F = 1000.0F;

        public Component accept(Object... args) {
            switch (this) {
                case STATE -> {
                    boolean state = (boolean) args[0];

                    return state ? A : D;
                }
                case COOLDOWN -> {
                    long currentMills = ((long) args[0]);
                    long lastMills = ((long) args[1]);
                    long cooldown = ((long) args[2]);

                    double cd = Math.max((cooldown - currentMills + lastMills) / SECOND_MILLS_F, 0);

                    return cd == 0 ? A : cooldown(cd);
                }
                case DURATION -> {
                    long currentMills = ((long) args[0]);
                    long lastMills = ((long) args[1]);
                    long duration = ((long) args[2]);

                    double remain = Math.max((duration - currentMills + lastMills) / SECOND_MILLS_F, 0);

                    return remain == 0 ? D : duration(remain);
                }
                case DURATION_COOLDOWN -> {
                    long currentMills = ((long) args[0]);
                    long lastMills = ((long) args[1]);
                    long cooldown = ((long) args[2]);
                    long duration = ((long) args[3]);

                    double cd = Math.max((cooldown - currentMills + lastMills) / SECOND_MILLS_F, 0);
                    double remain = Math.max(duration / SECOND_MILLS_F, 0);

                    return remain == 0 ? (cd == 0 ? A : cooldown(cd)) : duration(remain);
                }
                case COMBO -> {
                    int count = (int) args[0];
                    int max = (int) args[1];

                    return count >= max ? A : combo(count, max);
                }
                case COMBO_COOLDOWN -> {
                    long currentMills = ((long) args[0]);
                    long lastMills = ((long) args[1]);
                    long cooldown = ((long) args[2]);
                    int count = (int) args[3];
                    int max = (int) args[4];

                    double cd = Math.max((cooldown - currentMills + lastMills) / SECOND_MILLS_F, 0);

                    return cd == 0 ? (count >= max ? A : combo(count, max)) : cooldown(cd);
                }
                case COMBO_STATE -> {
                    int count = (int) args[0];
                    int max = (int) args[1];
                    boolean state = (boolean) args[2];

                    return state ? count >= max ? A : combo(count, max) : D;
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

        private static Component combo(int count, int max) {
            return C.arguments(Component.text(count, NamedTextColor.GRAY), Component.text(max, NamedTextColor.GREEN));
        }

        private static Component mode(Component mode) {
            return mode.color(NamedTextColor.GREEN);
        }
    }
}
