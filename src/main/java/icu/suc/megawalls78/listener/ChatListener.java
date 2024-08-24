package icu.suc.megawalls78.listener;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.management.GameManager;
import icu.suc.megawalls78.util.LP;
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
        Component name;
        if (MegaWalls78.getInstance().getGameManager().inFighting()) {
            GameManager gameManager = MegaWalls78.getInstance().getGameManager();
            NamedTextColor color;
            Component chat;
            if (gameManager.isSpectator(player)) {
                color = NamedTextColor.GRAY;
                chat = Component.translatable("mw78.team.spec.chat");
            } else {
                GamePlayer gamePlayer = gameManager.getPlayer(player);
                color = gamePlayer.getTeam().color();
                chat = gamePlayer.getTeam().chat();
            }
            name = Component.translatable("mw78.brackets", color, chat)
                    .append(Component.space())
                    .append(LP.getPrefix(player.getUniqueId()))
                    .append(player.teamDisplayName().color(LP.getNameColor(player.getUniqueId())));
        } else {
            name = LP.getPrefix(player.getUniqueId())
                    .append(player.teamDisplayName().color(LP.getNameColor(player.getUniqueId())));
            Identity identity = MegaWalls78.getInstance().getIdentityManager().getRankedIdentity(player.getUniqueId());
            if (identity != null) {
                name = identity.getIcon().color(MegaWalls78.getInstance().getIdentityManager().getIdentityColor(player.getUniqueId(), identity)).append(Component.space()).append(name);
            }
        }
        return Component.translatable("mw78.chat", NamedTextColor.GRAY, name, component1.color(LP.getChatColor(player.getUniqueId())));
    }
}
