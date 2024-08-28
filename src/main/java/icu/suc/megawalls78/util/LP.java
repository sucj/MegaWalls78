package icu.suc.megawalls78.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.checkerframework.checker.units.qual.A;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class LP {

    private static final LuckPerms API = LuckPermsProvider.get();

    public static String getGroup(UUID uuid) {
        return getUser(uuid).getPrimaryGroup();
    }

    public static Component getPrefix(String group) {
        Group g = API.getGroupManager().getGroup(group);
        if (g == null) {
            try {
                Optional<Group> optional = API.getGroupManager().loadGroup(group).get();
                if (optional.isPresent()) {
                    g = optional.get();
                } else {
                    return Component.empty();
                }
            } catch (InterruptedException | ExecutionException e) {
                return Component.empty();
            }
        }
        try {
            return MiniMessage.miniMessage().deserialize(Objects.requireNonNull(g.getCachedData().getMetaData().getPrefix())).appendSpace();
        } catch (NullPointerException e) {
            return Component.empty();
        }
    }

    public static NamedTextColor getNameColor(String group) {
        Group g = API.getGroupManager().getGroup(group);
        if (g == null) {
            try {
                Optional<Group> optional = API.getGroupManager().loadGroup(group).get();
                if (optional.isPresent()) {
                    g = optional.get();
                } else {
                    return null;
                }
            } catch (InterruptedException | ExecutionException e) {
                return null;
            }
        }
        try {
            return NamedTextColor.NAMES.value(Objects.requireNonNull(g.getCachedData().getMetaData().getMetaValue("color")));
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static Component getPrefix(UUID uuid) {
        return getPrefix(getGroup(uuid));
//        try {
//            return MiniMessage.miniMessage().deserialize(Objects.requireNonNull(getUser(uuid).getCachedData().getMetaData().getPrefix())).appendSpace();
//        } catch (NullPointerException e) {
//            return Component.empty();
//        }
    }

    public static NamedTextColor getNameColor(UUID uuid) {
        return getNameColor(getGroup(uuid));
//        try {
//            return NamedTextColor.NAMES.value(Objects.requireNonNull(getUser(uuid).getCachedData().getMetaData().getMetaValue("color")));
//        } catch (NullPointerException e) {
//            return null;
//        }
    }

    public static TextColor getChatColor(UUID uuid) {
        try {
            return TextColor.fromHexString(Objects.requireNonNull(getUser(uuid).getCachedData().getMetaData().getMetaValue("chat")));
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static User getUser(UUID uuid) {
        User user = API.getUserManager().getUser(uuid);
        if (user == null) {
            try {
                user = API.getUserManager().loadUser(uuid).get();
            } catch (InterruptedException | ExecutionException ignored) {
            }
        }
        return user;
    }
}
