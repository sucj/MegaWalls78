package icu.suc.megawalls78.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LP {
    private static final LuckPerms API = LuckPermsProvider.get();

    public static Component getPrefix(UUID uuid) {
        String prefix = API.getUserManager().getUser(uuid).getCachedData().getMetaData().getPrefix();
        if (prefix == null) {
            return Component.empty();
        }
        return MiniMessage.miniMessage().deserialize(prefix).append(Component.space());
    }

    public static String getPerm(UUID uuid) {
        return API.getUserManager().getUser(uuid).getPrimaryGroup();
    }

    public static NamedTextColor getNameColor(UUID uuid) {
        return NamedTextColor.NAMES.value(API.getUserManager().getUser(uuid).getCachedData().getMetaData().getMetaValue("color"));
    }

    public static TextColor getChatColor(UUID uuid) {
        return TextColor.fromHexString(API.getUserManager().getUser(uuid).getCachedData().getMetaData().getMetaValue("chat"));
    }
}
