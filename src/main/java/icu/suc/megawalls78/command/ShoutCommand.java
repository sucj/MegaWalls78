package icu.suc.megawalls78.command;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.management.GameManager;
import icu.suc.megawalls78.util.LP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

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
                    UUID uuid = player.getUniqueId();
                    GamePlayer gamePlayer = gameManager.getPlayer(uuid);
                    Bukkit.broadcast(Component.translatable("mw78.brackets", NamedTextColor.GOLD, Component.translatable("mw78.shout")).appendSpace().append(Component.translatable("mw78.chat", NamedTextColor.GRAY, Component.translatable("mw78.brackets", gamePlayer.getTeam().color(), gamePlayer.getTeam().chat()).appendSpace().append(LP.getPrefix(uuid)).append(player.name().color(LP.getNameColor(uuid))).hoverEvent(player).clickEvent(player.teamDisplayName().clickEvent()), Component.text(String.join(" ", strings), NamedTextColor.WHITE))));
                }
                return true;
            }
        }
        return false;
    }
}
