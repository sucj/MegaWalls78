package icu.suc.megawalls78.command;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.management.GameManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class EnergyCommand extends Command {

    public static LiteralCommandNode<CommandSourceStack> register(String name, String permission) {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        return Commands.literal(name)
                .requires(source -> {
                    if (hasPermission(source, permission)) {
                        return gameManager.inFighting() && source.getSender() instanceof Player player && !gameManager.isSpectator(player);
                    }
                    return false;
                })
                .executes(context -> {
                    GamePlayer player = gameManager.getPlayer(((Player) context.getSource().getExecutor()));
                    player.setEnergy(player.getIdentity().getEnergy());
                    return 0;
                })
                .then(Commands.argument("energy", FloatArgumentType.floatArg())
                        .executes(context -> {
                            gameManager.getPlayer(((Player) context.getSource().getExecutor())).setEnergy(context.getArgument("energy", Float.class));
                            return 0;
                        })
                )
                .build();
    }
}
