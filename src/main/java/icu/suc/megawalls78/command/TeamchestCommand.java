package icu.suc.megawalls78.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.management.GameManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class TeamchestCommand extends Command {

    public static LiteralCommandNode<CommandSourceStack> register(String name, String permission) {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        return Commands.literal(name)
                .requires(source -> {
                    if (hasPermission(source, permission)) {
                        if (gameManager.inFighting()) {
                            return source.getExecutor() instanceof Player player && !gameManager.isSpectator(player);
                        }
                    }
                    return false;
                })
                .executes(context -> {
                    Player player = (Player) context.getSource().getExecutor();
                    player.openInventory(gameManager.teamChest(gameManager.getPlayer(player).getTeam()));
                    return 0;
                })
                .build();
    }
}
