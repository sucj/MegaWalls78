package icu.suc.megawalls78.listener;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.management.GameManager;
import icu.suc.megawalls78.util.LP;
import icu.suc.megawalls78.util.ComponentUtil;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ChatListener implements Listener, ChatRenderer {

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        if (gameManager.inFighting()) {
            Player player = event.getPlayer();
            Set<Audience> viewers = event.viewers();
            viewers.clear();
            viewers.add(Bukkit.getConsoleSender());
            if (gameManager.isSpectator(player)) {
                viewers.addAll(gameManager.getSpectators());
            } else {
                viewers.addAll(gameManager.getTeammates(player));
            }
        }
        event.renderer(this);
    }

    @Override
    public @NotNull Component render(@NotNull Player player, @NotNull Component component, @NotNull Component component1, @NotNull Audience audience) {
        Component formatted;
        if (MegaWalls78.getInstance().getGameManager().inFighting()) {
            GamePlayer gamePlayer = MegaWalls78.getInstance().getGameManager().getPlayer(player);
            formatted = Component.translatable("ms78.brackets", gamePlayer.getTeam().color(), gamePlayer.getTeam().chat())
                    .append(ComponentUtil.BLANK_COMPONENT)
                    .append(LP.getPrefix(player))
                    .append(player.displayName().color(LP.getNameColor(player)))
                    .append(Component.translatable("mw78.sb.colon", NamedTextColor.GRAY))
                    .append(ComponentUtil.BLANK_COMPONENT);
        } else {
            formatted = LP.getPrefix(player)
                    .append(player.displayName().color(LP.getNameColor(player)))
                    .append(Component.translatable("mw78.sb.colon", NamedTextColor.GRAY))
                    .append(ComponentUtil.BLANK_COMPONENT);
        }
        return formatted.append(component1.color(LP.getChatColor(player)));
    }
}
