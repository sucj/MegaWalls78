package icu.suc.megawalls78.management;

import com.google.common.collect.Maps;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.identity.Identity;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.sql.*;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class IdentityManager {

    private final Map<UUID, Identity> rankCache;
    private final Map<UUID, Boolean> rankCached;
    private final Map<UUID, Map<Identity, NamedTextColor>> colorCache;

    public IdentityManager() {
        this.rankCache = Maps.newHashMap();
        this.rankCached = Maps.newHashMap();
        this.colorCache = Maps.newHashMap();
    }

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
        NamedTextColor color = getColorCached(player, identity);
        if (color == null) {
            CompletableFuture<String> future = MegaWalls78.getInstance().getDatabaseManager().getIdentityColor(player, identity);
            try {
                color = NamedTextColor.NAMES.value(future.get());
                if (color == null) {
                    color = NamedTextColor.GRAY;
                }
                setColorCached(player, identity, color);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return color;
    }

    public Identity getRankedIdentity(UUID player) {
        if (isRankCached(player)) {
            return rankCache.get(player);
        }
        CompletableFuture<String> future = MegaWalls78.getInstance().getDatabaseManager().getRankedIdentity(player);
        try {
            Identity identity = Identity.getIdentity(future.get());
            rankCache.put(player, identity);
            rankCached.put(player, true);
            return identity;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isRankCached(UUID player) {
        return rankCached.getOrDefault(player, false);
    }

    public NamedTextColor getColorCached(UUID player, Identity identity) {
        Map<Identity, NamedTextColor> colorMap = colorCache.get(player);
        if (colorMap == null) {
            return null;
        }
        return colorMap.get(identity);
    }

    public void setColorCached(UUID player, Identity identity, NamedTextColor color) {
        Map<Identity, NamedTextColor> colorMap = colorCache.get(player);
        if (colorMap == null) {
            colorMap = Maps.newHashMap();
            colorMap.put(identity, color);
            colorCache.put(player, colorMap);
        }
        colorMap.put(identity, color);
    }

    public void clearCache(UUID player) {
        colorCache.remove(player);
        rankCache.remove(player);
        rankCached.remove(player);
    }
}
