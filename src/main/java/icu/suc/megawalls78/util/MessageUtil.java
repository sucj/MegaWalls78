package icu.suc.megawalls78.util;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MessageUtil {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy");
    public static final DateTimeFormatter MMSS_FORMATTER = DateTimeFormatter.ofPattern("mm:ss");

    public static final Title.Times ONE_SEC_TIMES = Title.Times.times(Duration.ZERO, Duration.ofSeconds(1L), Duration.ZERO);
    public static final Title.Times DEFAULT_TIMES = Title.Times.times(Ticks.duration(10L), Ticks.duration(70L), Ticks.duration(20L));

    public static final Component BLANK_COMPONENT = Component.text(" ");

    private static TextColor secondColor(long millis) {
        TextColor color = NamedTextColor.GREEN;
        if (millis <= 3000L) {
            color = NamedTextColor.RED;
        } else if (millis <= 5000L) {
            color = NamedTextColor.GOLD;
        } else if (millis <= 10000L) {
            color = NamedTextColor.YELLOW;
        }
        return color;
    }

    public static Component second(long millis) {
        return Component.text(millis / 1000L, secondColor(millis));
    }

    public static Component mmss(long millis) {
        return Component.text(MMSS_FORMATTER.format(LocalTime.ofSecondOfDay(millis / 1000L)), secondColor(millis));
    }

    public static void sendTitle(Component title, Component subtitle, Title.Times times, Audience audience) {
        audience.showTitle(Title.title(title, subtitle, times));
    }

    public static void sendTitle(Component title, Component subtitle, Title.Times times, Iterable<? extends Audience> audiences) {
        sendTitle(title, subtitle, times, Audience.audience(audiences));
    }

    public static void sendMessage(Component message, Audience audience) {
        audience.sendMessage(message);
    }

    public static void sendMessage(Component message, Iterable<? extends Audience> audiences) {
        sendMessage(message, Audience.audience(audiences));
    }
}
