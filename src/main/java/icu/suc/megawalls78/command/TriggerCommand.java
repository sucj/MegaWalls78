package icu.suc.megawalls78.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import icu.suc.megawalls78.gui.TriggerGui;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class TriggerCommand extends Command {

    public static LiteralCommandNode<CommandSourceStack> register(String name, String permission) {
        return Commands.literal(name)
                .requires(source -> {
                    if (hasPermission(source, permission)) {
                        return source.getExecutor() instanceof Player;
                    }
                    return false;
                })
                .executes(context -> {
                    TriggerGui.open((Player) context.getSource().getExecutor());
                    return 0;
                })
                .build();
    }
}
