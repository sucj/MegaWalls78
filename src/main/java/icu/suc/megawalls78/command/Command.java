package icu.suc.megawalls78.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;

public abstract class Command {

    protected static boolean hasPermission(CommandSourceStack source, String permission) {
        return source.getSender().hasPermission(permission);
    }
}
