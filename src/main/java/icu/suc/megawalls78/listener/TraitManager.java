package icu.suc.megawalls78.listener;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import icu.suc.megawalls78.identity.Identity;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;

public class TraitManager {

    private static final Map<Identity, Book> PAGES;

    public static Book book(Identity identity) {
        return PAGES.get(identity);
    }

    static {
        PAGES = Maps.newHashMap();
        for (Identity identity : Identity.values()) {
            List<Component> pages = Lists.newArrayList();
            Component name = identity.getName();
            pages.add(name);
            PAGES.put(identity, Book.book(name, name, pages));
        }
    }
}
