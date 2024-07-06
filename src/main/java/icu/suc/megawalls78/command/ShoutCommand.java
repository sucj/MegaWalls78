package icu.suc.megawalls78.command;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.management.GameManager;
import icu.suc.megawalls78.util.LP;
import icu.suc.megawalls78.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ShoutCommand implements CommandExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
    if (commandSender instanceof Player player) {
      if (strings.length >= 1) {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        if (gameManager.isSpectator(player)) {
          return true;
        }
        if (gameManager.inFighting()) {
          GamePlayer gamePlayer = gameManager.getPlayer(player);
          Bukkit.broadcast(Component.translatable("ms78.brackets", NamedTextColor.GOLD, Component.translatable("mw78.shout"))
            .append(MessageUtil.BLANK_COMPONENT)
            .append(Component.translatable("ms78.brackets", gamePlayer.getTeam().color(), gamePlayer.getTeam().chat()))
            .append(MessageUtil.BLANK_COMPONENT)
            .append(LP.getPrefix(player))
            .append(player.displayName().color(LP.getNameColor(player)))
            .append(Component.translatable("mw78.sb.colon", NamedTextColor.WHITE))
            .append(MessageUtil.BLANK_COMPONENT)
            .append(Component.text(String.join(" ", strings), NamedTextColor.WHITE)));
        }
        return true;
      }
    }
    return false;
  }
}
