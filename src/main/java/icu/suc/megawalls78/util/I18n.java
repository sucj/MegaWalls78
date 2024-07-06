package icu.suc.megawalls78.util;

import icu.suc.megawalls78.MegaWalls78;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import org.bukkit.NamespacedKey;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;

public class I18n {

    public static void register(Map<Locale, Map<String, MessageFormat>> formats) {
        TranslationRegistry registry = TranslationRegistry.create(new NamespacedKey(MegaWalls78.getInstance(), "lang"));
        for (Locale locale : formats.keySet()) {
            registry.registerAll(locale, formats.get(locale));
        }
        GlobalTranslator.translator().addSource(registry);
    }
}
