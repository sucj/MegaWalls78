package icu.suc.megawalls78.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.management.GameManager;
import icu.suc.megawalls78.management.TraitManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class TraitCommand extends Command {

    public static LiteralCommandNode<CommandSourceStack> register(String name, String permission) {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        return Commands.literal(name)
                .requires(source -> {
                    if (hasPermission(source, permission)) {
                        return source.getExecutor() instanceof Player player && gameManager.getPlayer(player) instanceof GamePlayer gamePlayer && gamePlayer.getIdentity() != null;
                    }
                    return false;
                })
                .executes(context -> {
                    Player player = (Player) context.getSource().getExecutor();
                    player.openBook(TraitManager.book(gameManager.getPlayer(player).getIdentity()));
                    return 0;
                })
                .build();
    }
}
