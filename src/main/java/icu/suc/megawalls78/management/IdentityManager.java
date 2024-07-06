package icu.suc.megawalls78.management;

import icu.suc.megawalls78.identity.Identity;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.UUID;

public class IdentityManager {

    public Identity getPlayerIdentity(UUID player) {
        // TODO
        return Identity.HEROBRINE;
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
