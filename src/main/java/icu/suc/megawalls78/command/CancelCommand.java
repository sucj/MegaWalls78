package icu.suc.megawalls78.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GameRunner;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class CancelCommand extends Command {

    public static LiteralCommandNode<CommandSourceStack> register(String name, String permission) {
        return Commands.literal(name)
                .requires(source -> {
                    if (hasPermission(source, permission)) {
                        return MegaWalls78.getInstance().getGameManager().inWaiting();
                    }
                    return false;
                })
                .executes(context -> {
                    GameRunner.force = false;
                    return 0;
                })
                .build();
    }
}
