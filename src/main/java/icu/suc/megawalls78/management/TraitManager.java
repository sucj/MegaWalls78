package icu.suc.megawalls78.management;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.trait.Trait;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public class TraitManager {

    private static final Map<Class<? extends Trait>, String> ID_MAP = Maps.newHashMap();
    private static final Map<Class<? extends Trait>, Component> NAME_MAP = Maps.newHashMap();
    private static final Map<Identity, Book> PAGES = Maps.newHashMap();

    public static <T extends Trait> T trait(Class<? extends T> clazz, GamePlayer player) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return trait(clazz, player, null);
    }

    public static <T extends Trait> T trait(Class<? extends T> clazz, GamePlayer player, String id) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        id = id == null ? clazz.getAnnotation(icu.suc.megawalls78.identity.trait.annotation.Trait.class).value() : id;
        Component name = Component.translatable("mw78.trait." + id);
        ID_MAP.put(clazz, id);
        NAME_MAP.put(clazz, name);
        T trait = clazz.getConstructor().newInstance();
        trait.PLAYER(player);
        return trait;
    }

    public static String id(Class<? extends Trait> clazz) {
        return ID_MAP.get(clazz);
    }

    public static Component name(Class<? extends Trait> clazz) {
        return NAME_MAP.get(clazz);
    }

    public static Book book(Identity identity) {
        return PAGES.get(identity);
    }

    static {
        for (Identity identity : Identity.values()) {
            List<Component> pages = Lists.newArrayList();
            Component name = identity.getName();
            pages.add(name);
            PAGES.put(identity, Book.book(name, name, pages));
        }
    }
}
