package icu.suc.megawalls78.management;

import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.gui.SkinGui;
import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.Skin;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.property.InputDataResult;
import net.skinsrestorer.api.property.SkinProperty;
import org.bukkit.entity.Player;

import java.util.*;

public class SkinManager {

    private final Map<Identity, List<Skin>> skins;
    private final Map<Player, Map<Identity, Skin>> playerSelectedSkin;

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
        this.applySkin(player, identity, getPlayerSelectedSkin(player, identity));
    }

    public void applySkin(Player player, Identity identity, Skin skin) {
        SkinsRestorer skinsRestorer = SkinsRestorerProvider.get();
        Optional<InputDataResult> result = skinsRestorer.getSkinStorage().findSkinData(customSkin(identity, skin));
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

    public Skin getPlayerSelectedSkin(Player player, Identity identity) {
        // TODO DATABASE
        return playerSelectedSkin.computeIfAbsent(player, k -> Maps.newHashMap()).computeIfAbsent(identity, k -> skins.get(identity).getFirst());
    }

    public Skin setPlayerSelectedSkin(Player player, Identity identity, Skin skin) {
        // TODO DATABASE
        applySkin(player, identity, skin);
        Skin put = playerSelectedSkin.computeIfAbsent(player, k -> Maps.newHashMap()).put(identity, skin);
        player.getInventory().setItem(1, SkinGui.trigger(player));
        return put;
    }

    public void addSkin(Identity identity, Skin skin) {
        skins.get(identity).add(skin);
        SkinsRestorerProvider.get().getSkinStorage().setCustomSkinData(customSkin(identity, skin), SkinProperty.of(skin.value(), skin.signature()));
    }

    public List<Skin> getSkins(Identity identity) {
        return skins.get(identity);
    }

    private String customSkin(Identity identity, Skin skin) {
        return identity.getId() + "." + skin.id();
    }
}
