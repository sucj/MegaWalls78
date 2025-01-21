package icu.suc.megawalls78.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GameState;
import icu.suc.megawalls78.management.GameManager;
import icu.suc.megawalls78.util.RandomUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class SurfaceCommand extends Command {

    public static LiteralCommandNode<CommandSourceStack> register(String name, String permission) {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        return Commands.literal(name)
                .requires(source -> {
                    if (hasPermission(source, permission)) {
                        if (gameManager.getState().equals(GameState.PREPARING)) {
                            return source.getExecutor() instanceof Player player && !gameManager.isSpectator(player);
                        }
                    }
                    return false;
                })
                .executes(context -> {
                    Player player = (Player) context.getSource().getExecutor();
                    player.teleport(RandomUtil.getRandomSpawn(gameManager.getPlayer(player).getTeam().spawn()));
                    return 0;
                })
                .build();
    }
}
