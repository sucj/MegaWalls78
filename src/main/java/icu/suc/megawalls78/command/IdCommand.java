package icu.suc.megawalls78.command;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.management.GameManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IdCommand implements CommandExecutor, TabExecutor {

    private static List<String> identities;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player player) {
            MegaWalls78 instance = MegaWalls78.getInstance();
            GameManager gameManager = instance.getGameManager();
            if (gameManager.inFighting() || !gameManager.inWaiting()) {
                return true;
            }
            if (strings.length == 1) {
                Identity identity = Identity.getIdentity(strings[0]);
                if (identity == null) {
                    commandSender.sendMessage(Component.translatable("mw78.identity.failed", NamedTextColor.RED));
                    return true;
                }
                gameManager.getPlayer(player).setIdentity(identity);
                commandSender.sendMessage(Component.translatable("mw78.identity", NamedTextColor.GREEN, identity.getName().color(instance.getIdentityManager().getIdentityColor(player.getUniqueId(), identity))));
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (strings.length == 1) {
            return getIdentities(strings[0]);
        }

        return List.of();
    }

    private static List<String> getIdentities(String name) {
        if (identities == null) {
            identities = Arrays.stream(Identity.values())
                    .map(Identity::getId)
                    .collect(Collectors.toList());
        }
        return identities.stream()
                .filter(s -> s.contains(name.toLowerCase()))
                .toList();
    }
}
