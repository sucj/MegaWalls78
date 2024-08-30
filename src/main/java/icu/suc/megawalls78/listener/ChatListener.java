package icu.suc.megawalls78.listener;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.game.record.GameTeam;
import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.management.GameManager;
import icu.suc.megawalls78.management.IdentityManager;
import icu.suc.megawalls78.util.LP;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.command.PaperCommand;
import io.papermc.paper.command.PaperPluginsCommand;
import io.papermc.paper.command.brigadier.PaperBrigadier;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

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

        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        IdentityManager identityManager = MegaWalls78.getInstance().getIdentityManager();

        UUID uuid = player.getUniqueId();

        if (gameManager.inFighting()) {
            NamedTextColor color;
            Component chat;
            if (gameManager.isSpectator(uuid)) {
                color = GameTeam.SPEC.color();
                chat = GameTeam.SPEC.chat();
            } else {
                GamePlayer gamePlayer = gameManager.getPlayer(uuid);
                color = gamePlayer.getTeam().color();
                chat = gamePlayer.getTeam().chat();
            }
            name = Component.translatable("mw78.brackets", color, chat)
                    .appendSpace()
                    .append(LP.getPrefix(uuid))
                    .append(player.name().color(LP.getNameColor(uuid)));
        } else {
            name = LP.getPrefix(uuid)
                    .append(player.name().color(LP.getNameColor(uuid)));
            Identity identity = identityManager.getRankedIdentity(uuid);
            if (identity != null) {
                name = identity.getIcon().color(identityManager.getIdentityColor(uuid, identity)).appendSpace().append(name);
            }
        }
        return Component.translatable("mw78.chat", NamedTextColor.GRAY, name.hoverEvent(player).clickEvent(player.teamDisplayName().clickEvent()), component1.color(LP.getChatColor(uuid)));
    }
}
