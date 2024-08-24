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
                    Bukkit.broadcast(Component.translatable("mw78.brackets", NamedTextColor.GOLD, Component.translatable("mw78.shout"))
                            .append(Component.space())
                            .append(Component.translatable("mw78.brackets", gamePlayer.getTeam().color(), gamePlayer.getTeam().chat()))
                            .append(Component.space())
                            .append(LP.getPrefix(player.getUniqueId()))
                            .append(player.teamDisplayName().color(LP.getNameColor(player.getUniqueId())))
                            .append(Component.translatable("mw78.sb.colon", NamedTextColor.WHITE))
                            .append(Component.space())
                            .append(Component.text(String.join(" ", strings), NamedTextColor.WHITE)));
                }
                return true;
            }
        }
        return false;
    }
}
