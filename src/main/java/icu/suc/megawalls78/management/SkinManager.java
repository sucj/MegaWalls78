package icu.suc.megawalls78.management;

import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.Skin;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.property.InputDataResult;
import net.skinsrestorer.api.property.SkinProperty;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class SkinManager {

    private final Map<Identity, List<Skin>> skins;
    private final Map<UUID, Map<Identity, Skin>> playerSelectedSkin;

    private final Map<Player, SkinProperty> playerSkins;

    public SkinManager() {
        this.skins = Maps.newHashMap();
        for (Identity identity : Identity.values()) {
            skins.put(identity, Lists.newArrayList());
        }
        this.playerSelectedSkin = Maps.newHashMap();
        this.playerSkins = Maps.newHashMap();
    }

    public void applySkin(Player player) {
        this.applySkin(player, MegaWalls78.getInstance().getGameManager().getPlayer(player).getIdentity());
    }

    public void applySkin(Player player, Identity identity) {
        this.applySkin(player, getPlayerSelectedSkin(player.getUniqueId(), identity));
    }

    public void applySkin(Player player, Skin skin) {
        SkinsRestorer skinsRestorer = SkinsRestorerProvider.get();
        Optional<InputDataResult> result = skinsRestorer.getSkinStorage().findSkinData(skin.id());
        if (result.isEmpty()) {
            return;
        }
        skinsRestorer.getSkinApplier(Player.class).applySkin(player, result.get().getIdentifier());
    }

    public void resetSkin(Player player) {
        SkinsRestorerProvider.get().getSkinApplier(Player.class).applySkin(player, playerSkins.get(player));
    }

    public void addPlayerSkin(Player player) {
        for (ProfileProperty property : player.getPlayerProfile().getProperties()) {
            if (property.getName().equals(SkinProperty.TEXTURES_NAME)) {
                playerSkins.put(player, SkinProperty.of(property.getValue(), Objects.requireNonNull(property.getSignature())));
            }
        }
    }

    public Skin getPlayerSelectedSkin(UUID uuid, Identity identity) {
        Map<Identity, Skin> skinMap = playerSelectedSkin.computeIfAbsent(uuid, k -> Maps.newHashMap());
        Skin skin = skinMap.get(identity);
        if (skin == null) {
            String skinId;
            try {
                skinId = MegaWalls78.getInstance().getDatabaseManager().getIdentitySkin(uuid, identity).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            for (List<Skin> skinList : skins.values()) {
                boolean flag = false;
                for (Skin aSkin : skinList) {
                    if (aSkin.id().equals(skinId)) {
                        skin = aSkin;
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    break;
                }
            }
        }
        if (skin == null) {
            skin = skins.get(identity).getFirst();
        }
        Skin finalSkin = skin;
        return skinMap.computeIfAbsent(identity, k -> finalSkin);
    }

    public void setPlayerSelectedSkin(UUID uuid, Identity identity, Skin skin) {
        MegaWalls78.getInstance().getDatabaseManager().setIdentitySkin(uuid, identity, skin);
        playerSelectedSkin.computeIfAbsent(uuid, k -> Maps.newHashMap()).put(identity, skin);
    }

    public void addSkin(Identity identity, Skin skin) {
        skins.get(identity).add(skin);
        SkinsRestorerProvider.get().getSkinStorage().setCustomSkinData(skin.id(), SkinProperty.of(skin.value(), skin.signature()));
    }

    public List<Skin> getSkins(Identity identity) {
        return skins.get(identity);
    }
}
