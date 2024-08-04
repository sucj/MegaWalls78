package icu.suc.megawalls78.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class LP {

    private static final LuckPerms API = LuckPermsProvider.get();

    public static Component getPrefix(UUID uuid) {
        try {
            return MiniMessage.miniMessage().deserialize(getUser(uuid).getCachedData().getMetaData().getPrefix()).append(Component.space());
        } catch (NullPointerException e) {
            return Component.empty();
        }
    }

    public static NamedTextColor getNameColor(UUID uuid) {
        try {
            return NamedTextColor.NAMES.value(getUser(uuid).getCachedData().getMetaData().getMetaValue("color"));
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static TextColor getChatColor(UUID uuid) {
        try {
            return TextColor.fromHexString(getUser(uuid).getCachedData().getMetaData().getMetaValue("chat"));
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
