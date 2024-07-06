package icu.suc.megawalls78.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.entity.Player;

public class LP {
  private static final LuckPerms API = LuckPermsProvider.get();

  public static Component getPrefix(Player player) {
    String prefix = API.getUserManager().getUser(player.getUniqueId()).getCachedData().getMetaData().getPrefix();
    if (prefix == null) {
      return Component.empty();
    }
    return MiniMessage.miniMessage().deserialize(prefix).append(MessageUtil.BLANK_COMPONENT);
  }

  public static String getPerm(Player player) {
    return API.getUserManager().getUser(player.getUniqueId()).getPrimaryGroup();
  }

  public static NamedTextColor getNameColor(Player player) {
    return NamedTextColor.NAMES.value(API.getUserManager().getUser(player.getUniqueId()).getCachedData().getMetaData().getMetaValue("color"));
  }

  public static TextColor getChatColor(Player player) {
    return TextColor.fromHexString(API.getUserManager().getUser(player.getUniqueId()).getCachedData().getMetaData().getMetaValue("chat"));
  }
}
