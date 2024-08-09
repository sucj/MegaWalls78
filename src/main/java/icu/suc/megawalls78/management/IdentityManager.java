package icu.suc.megawalls78.management;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.identity.Identity;
import net.kyori.adventure.text.format.NamedTextColor;

import java.sql.*;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class IdentityManager {

    public Identity getPlayerIdentity(UUID player) {
        CompletableFuture<String> future = MegaWalls78.getInstance().getDatabaseManager().getPlayerIdentity(player);
        String id;
        try {
            id = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return Objects.requireNonNullElse(Identity.getIdentity(id), Identity.HEROBRINE);
    }

    public NamedTextColor getIdentityColor(UUID player, Identity identity) {
        // TODO
        return NamedTextColor.GRAY;
    }

    public Identity getRankedIdentity(UUID player) {
        // TODO
        return null;
    }
}
